package com.github.aucguy.optifinegradle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectHelper
{
    public static Method getMethod(Class<?> clazz, String name, Class<?> ... parameters)
    {
        Method method = null;
        while(clazz != null && method == null)
        {
            try
            {
                method = clazz.getDeclaredMethod(name, parameters);
            }
            catch(NoSuchMethodException | SecurityException e)
            {
            }
            clazz = clazz.getSuperclass();
        }
        if(method == null)
        {
            throw(new RuntimeException(name + " method not found on class " + clazz.getName()));
        }
        method.setAccessible(true);
        return method;
    }
    
    public static Object invoke(Method method, Object self, Object ... parameters)
    {
        try
        {
            return method.invoke(self, parameters);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw(new RuntimeException(e));
        }
    }

    public static Field getField(Class<?> clazz, String name)
    {
        Field field = null;
        while(clazz != null && field == null)
        {
            try
            {
                field = clazz.getDeclaredField(name);
            }
            catch(NoSuchFieldException | SecurityException e)
            {
            }
            clazz = clazz.getSuperclass();
        }
        if(field == null)
        {
            throw(new RuntimeException(name + " method not found on class " + clazz.getName()));
        }
        field.setAccessible(true);
        return field;
    }

    public static Object get(Field field, Object self)
    {
        try
        {
            return field.get(self);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw(new RuntimeException(e));
        }
    }
}
