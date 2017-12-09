package com.github.aucguy.optifinegradle.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;

import com.github.aucguy.optifinegradle.AsmProcessingTask;
import com.github.aucguy.optifinegradle.FieldRenamer;

import net.minecraftforge.gradle.util.caching.Cached;
import net.minecraftforge.gradle.util.delayed.DelayedString;

public class JoinJars extends AsmProcessingTask
{
    @InputFile
    public Object       client;

    @InputFile
    public Object       optifine;

    @InputFile
    public Object       srg;
    
    @InputFile
    public Object		renames;
    
    @OutputFile
    @Cached
    public Object       obfuscatedClasses;
    
    @Input
    private Set<String> exclusions = new HashSet<String>();
    
    protected Remapper mapping;
    protected OutputStream obfClasses;

    @Override
    public void middle() throws IOException
    {
    	InputStream stream = manager.openFileForReading(renames);
    	Properties properties = new Properties();
    	properties.load(stream);
    	mapping = new SimpleRemapper((Map) properties);
    	obfClasses = manager.openFileForWriting(obfuscatedClasses);
    	copyJars(optifine, client);
    }

	@Override
	protected byte[] asRead(Object inJar, final String name, byte[] data)
	{
        if (inJar == optifine && name.endsWith(".class"))
        {
            try
            {
				Patching.addObfClass(name, obfClasses);
			} catch (UnsupportedEncodingException e)
            {
				throw(new RuntimeException());
			} catch (IOException e)
            {
				throw(new RuntimeException());
			}
            data = processAsm(data, new TransformerFactory()
            {
				@Override
				public ClassVisitor create(ClassVisitor visitor)
				{
					ClassVisitor transformer = new FieldRenamer(visitor, name.substring(0, name.length() - 6));
					transformer = new ClassRemapper(transformer, mapping);
			        return transformer;
				}
            });
        }
        return data;
	}

    protected boolean acceptsFile(String file)
    {
        for (String i : exclusions)
        {
            if (file.startsWith(i))
                return false;
        }
        return true;
    }

    public void exclude(String excl)
    {
        exclusions.add(excl);
    }
    
    public static String resolveString(Object obj)
    {
    	if(obj instanceof DelayedString)
    	{
    		return ((DelayedString) obj).call();
    	}
    	return (String) obj;
    }
}