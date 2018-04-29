package com.github.aucguy.optifinegradle;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.OutputFiles;

import groovy.lang.Closure;
import net.minecraftforge.gradle.util.delayed.DelayedBase;
import net.minecraftforge.gradle.util.delayed.DelayedFile;
import net.minecraftforge.gradle.util.delayed.TokenReplacer;

public class TaskDuplicator
{
    public static final Field replacerField = ReflectHelper.getField(DelayedBase.class, "replacer");
    
    public static void duplicate(Project project, Object ... baseDependencies)
    {
        Set<Task> tasks = new HashSet<Task>();
        for(Object i : baseDependencies)
        {
            tasks.add(objToTask(project, i, true));
        }
        
        Set<File> duplicateTasks = new HashSet<File>();
        for(Task task : getDependedOn(project, tasks))
        {
            duplicateTask(project, task, duplicateTasks);
        }
    }
    
    public static Set<Task> getDependedOn(Project project, Set<Task> tasks)
    {
        tasks =  new HashSet<Task>(tasks);
        Set<Task> allTasks = new HashSet<Task>(project.getTasks());
        boolean added = true;
        while(added)
        {
            added = false;
            
            for(Task task : allTasks)
            {
                if(!tasks.contains(task))
                {
                    for(Object dependency : task.getDependsOn())
                    {
                        Task taskDependency = objToTask(project, dependency, false);
                        if(tasks.contains(taskDependency))
                        {
                            tasks.add(task);
                            added = true;
                        }
                    }
                }
            }
        }
        return tasks;
    }
    
    public static void duplicateTask(Project project, Task originalTask, Set<File> duplicateFiles)
    {
        Class<? extends Task> clazz = originalTask.getClass();
        if(clazz.getName().endsWith("_Decorated"))
        {
            clazz = (Class<? extends Task>) clazz.getSuperclass();
        }
        Task duplicateTask = project.getTasks().create(originalTask.getName() + "Optifine", clazz);
        for(Field field : getFields(clazz))
        {
            if(Modifier.isFinal(field.getModifiers()))
            {
                continue;
            }
            field.setAccessible(true);
            Object value = getField(field, originalTask);
            if(field.isAnnotationPresent(OutputFile.class))
            {
                value = mapOutputFieldValue(value, project, duplicateFiles); 
            }
            else if(field.isAnnotationPresent(OutputDirectory.class))
            {
                Set<Object> newValue = new HashSet<Object>();
                for(Object i : (Iterable<?>) value)
                {
                    newValue.add(mapOutputFieldValue(i, project, duplicateFiles));
                }
                value = newValue;
            }
            else if(field.isAnnotationPresent(InputFile.class))
            {
                value = mapInputFieldValue(value, project, duplicateFiles);
            } 
            else if(field.isAnnotationPresent(OutputFiles.class))
            {
                Set<Object> newValue = new HashSet<Object>();
                for(Object i : (Iterable<?>) value)
                {
                    newValue.add(mapInputFieldValue(i, project, duplicateFiles));
                }
                value = newValue;
            }
            setField(field, duplicateTask, value);
        }
    }

    private static Object mapOutputFieldValue(Object value, Project project, Set<File> duplicateFiles)
    {
        return mapFieldValue(value, project, false, new Function<File, File>()
        {
            @Override
            public File apply(File t)
            {
                duplicateFiles.add(t);
                return mapFile(t);
            }
        });
    }
    
    private static Object mapInputFieldValue(Object value, Project project, Set<File> duplicateFiles)
    {
        return mapFieldValue(value, project, true, new Function<File, File>()
        {
            @Override
            public File apply(File t)
            {
                if(duplicateFiles.contains(t))
                {
                    return mapFile(t);
                }
                return t;
            }
        });
    }

    private static List<Field> getFields(Class<?> clazz)
    {
        List<Field> fields = new LinkedList<Field>();
        while(clazz != null)
        {
            for(Field field : clazz.getDeclaredFields())
            {
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    protected static File mapFile(File value)
    {
        return new File(value.getPath().replace(".jar", "-optifine.jar"));
    }
    
    protected static Object mapFieldValue(Object value, Project project, boolean useClosure, Function<File, File> mapper)
    {
        //TODO support InputFiles annotation
        if(value instanceof File)
        {
            if(useClosure)
            {
                return new Closure(null)
                {
                    public Object call()
                    {
                        return mapper.apply((File) value);
                    }
                };
            }
            else
            {
                return mapper.apply((File) value);
            }
        }
        else if(value instanceof DelayedFile)
        {
            DelayedFile delayedFile = (DelayedFile) value;
            return new DelayedFile((Class<?>) delayedFile.getOwner(), project, (TokenReplacer) ReflectHelper.get(replacerField, delayedFile))
            {
                @Override
                public File resolveDelayed(String replaced)
                {
                    return mapFile(super.resolveDelayed(replaced));
                }
            };
        }
        else if(value instanceof Closure)
        {
            Closure closure = (Closure) value;
            final Object x = value;
            return new Closure(closure.getOwner(), closure.getThisObject())
            {
                public Object call()
                {
                    if(x instanceof File)
                    {
                        return mapper.apply((File) x);
                    }
                    else
                    {
                        return x;
                    }
                }
            };
        }
        else
        {
            return value;
        }
    }

    public static Object getField(Field field, Object instance)
    {
        try
        {
            return field.get(instance);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw(new RuntimeException(e));
        }
    }
    
    public static void setField(Field field, Object instance, Object value)
    {
        try
        {
            Class<?> type = field.getType();
            if(type.isPrimitive())
            {
                switch(type.getName())
                {
                    case "boolean": field.setBoolean(instance, ((Boolean) value).booleanValue()); break;
                    case "char": field.setChar(instance, ((Character) value).charValue()); break;
                    case "float": field.setFloat(instance, ((Float) value).floatValue()); break;
                    case "double": field.setDouble(instance, ((Double) value).doubleValue()); break;
                    case "byte": field.setByte(instance, ((Byte) value).byteValue()); break;
                    case "short": field.setShort(instance, ((Short) value).shortValue()); break;
                    case "int": field.setInt(instance, ((Integer) value).shortValue()); break;
                    case "long": field.setLong(instance, ((Long) value).longValue()); break;
                    default: throw(new RuntimeException("unknown primitive value"));
                }
            }
            else
            {
                field.set(instance, value);
            }
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public static Task objToTask(Project project, Object obj, boolean shouldFail)
    {
        if(obj instanceof Task)
        {
            return (Task) obj;
        }
        else if(obj instanceof String)
        {
            return project.getTasks().getByName((String) obj);
        }
        else if(shouldFail)
        {
            throw(new RuntimeException("task type is not a string"));
        }
        else
        {
            return null;
        }
    }
}
