package com.iciao.kanada;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Japanese dictionary class.<br>
 *
 * @author	Masahiko Sato
 */
public class kanwadict
{
	private static final String SRC_PATH		= "dictionary/japanese/";
	private	static final String	SRC_FILES	    = "kanwadict";
	private	static final String	KANWA_FILENAMES	= "kanwadict.dat";

	private static final int	SIZE_OF_LONG	= 8;

	private static kanwadict	kanwa			= new kanwadict();
	private	static boolean		init_faild		= false;

    static
    {
		File	kanwa_dict	= new File(SRC_PATH, KANWA_FILENAMES);

		if ( kanwa_dict.exists() )
		{
			System.out.println("Kanada: Found a pre-built Japanese dictionary.");
			try
			{
				kanwa.load_index(kanwa_dict);
				System.out.println("Kanada: Init completed!");
			}
			catch( Exception e )
			{
				System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
				init_faild	= true;
			}
		}
		else
		{
			System.out.println("Kanada: Building Japanese dictionary...");

			Calendar	start		= Calendar.getInstance();
			StringTokenizer	token	= new StringTokenizer(SRC_FILES, ",");

			try
			{
				Calendar	lap	= start;
				while ( token.hasMoreTokens() )
				{
					String source_file	 = token.nextToken();
					System.out.print("-> Loading Data: " + SRC_PATH + source_file + "... ");
					kanwa.load_data(SRC_PATH, source_file);
					Calendar	now		= Calendar.getInstance();
					int 	lap_time	= (int)Math.ceil(now.getTime().getTime() - lap.getTime().getTime());
					System.out.println("Done (" + lap_time + " ms)");
					lap	= now;
				}
			}
			catch( IOException e )
			{
				System.out.println("Kanada: Error!! Could not build dictionary: " + e);
				init_faild	= true;
			}

			if ( !init_faild )
			{
				int loading_time	= (int)Math.ceil((Calendar.getInstance().getTime().getTime() - start.getTime().getTime()));
				System.out.println("Kanada: Build Completed! (" + loading_time + " ms)");
				try
				{
					kanwa.build_dict(kanwa.kanwa_map);
					kanwa.load_index(kanwa_dict);
					System.out.println("Kanada: Init completed!");
				}
				catch( IOException e )
				{
					System.out.println("Kanada: Error!! Could not load the dictionary index: " + e);
					init_faild	= true;
				}
			}
		}
	}

	private void	load_index(File obj_file)	throws IOException
    {
		FileInputStream		file_stream		= new FileInputStream(obj_file);
		DataInputStream		data_stream		= new DataInputStream(file_stream);

		try
		{
			for ( int i = 0xb0; i <= 0xff; ++i )
			{
				for ( int j = 0x00; j <= 0xff; ++j )
				{
					kanwa_key		this_key		= new kanwa_key((char)i, (char)j);
					kanwa_address	this_address	= new kanwa_address();
					this_address.value	= data_stream.readLong();
					kanwa_index.put(this_key, this_address);
				}
			}
		}
		finally
		{
			file_stream.close();
			data_stream.close();
		}
	}

	private void	load_object(kanwa_key key)	throws Exception
    {
		File	obj_file;
		FileInputStream		file_stream		= null;
		BufferedInputStream	buffer			= null;
		ObjectInputStream	object_stream	= null;

		try
		{
			obj_file	= new File(SRC_PATH, KANWA_FILENAMES);
			file_stream		= new FileInputStream(obj_file);

			long	obj_address	= ((kanwa_address)kanwa_index.get(key)).value;

			file_stream.skip(obj_address);

			buffer			= new BufferedInputStream(file_stream);
			object_stream	= new ObjectInputStream(buffer);

			try
			{
				ArrayList	value_list	= (ArrayList)object_stream.readObject();
				kanwa_map.put(key, value_list);
			}
			catch ( ClassNotFoundException e )
			{
				throw new Exception(e.toString());
			}
		}
		finally
		{
			if ( object_stream != null )
				object_stream.close();

			if ( buffer != null )
				buffer.close();

			if ( file_stream != null )
				file_stream.close();
		}

    }

	private void	build_dict(final HashMap map)	throws IOException
    {
		File				out_file	= new File(SRC_PATH, KANWA_FILENAMES);

		if ( out_file.exists() )
		{
			out_file.delete();
			out_file.createNewFile();
			System.out.println("Creating a new dictionary...");
		}

		RandomAccessFile	dict_file	= new RandomAccessFile(out_file, "rw");

		// Create a space for key indices.
		for ( int i = 0xb0; i <= 0xff; ++i )
		{
			for ( int j = 0x00; j <= 0xff; ++j )
			{
				dict_file.writeLong(0);
			}
		}

		Iterator	iterator	= map.keySet().iterator();

  		while ( iterator.hasNext() )
		{
			kanwa_key	key		= (kanwa_key)iterator.next();

			long  pos = ( ( ( key.key_mbr[0] << 8 ) | key.key_mbr[1] ) - 0xb000 ) * SIZE_OF_LONG ;

			// Move to the key address.
			dict_file.seek(pos);
			dict_file.writeLong(dict_file.length());

			ByteArrayOutputStream	byte_array_stream 	= new ByteArrayOutputStream();
			ObjectOutputStream		object_stream		= new ObjectOutputStream(byte_array_stream);

			object_stream.writeObject((ArrayList)map.get(key));

			// Move to the end and append data.
			dict_file.seek(dict_file.length());
			dict_file.write(byte_array_stream.toByteArray());

			byte_array_stream.close();
			object_stream.close();
		}

		dict_file.close();
    }

	public static kanwadict get_kanwa()
	{
		return kanwa;
	}

	public kanwa_key get_key(char first, char second)
	{
		return new kanwa_key(first, second);
	}

	public ArrayList get_value(kanwa_key key)
	{
		return (ArrayList)kanwa_map.get(key);
	}

	public boolean search_key(kanwa_key key) throws Exception
	{
		kanwa_address this_address	= (kanwa_address)kanwa_index.get(key);
		if ( this_address != null && !kanwa_map.containsKey(key) && this_address.value > 0 )
		{
			load_object(key);
		}
		return kanwa_map.containsKey(key);
	}

	private void load_data(String filepath, String filename) throws IOException
	{
		File	kanwa_file			= new File(filepath, filename);

		if ( !kanwa_file.exists() )
		{
			System.out.println("Dictionary File Not Found: " + filepath + filename);
			init_faild	= true;
			return;
		}

		InputStreamReader	file_stream	= new InputStreamReader(new FileInputStream(kanwa_file), kanada_def.JDK_JIS_AUTO_DETECT);
		BufferedReader		reader		= new BufferedReader(file_stream);
		try
		{
			for(;;)
			{
				String	line	= reader.readLine();

				if ( line == null )
				{
					return;
				}

				line = new String(line.getBytes(kanada_def.JDK_EUC_JP), kanada_def.JDK_ISO8859_1);
				line = line.trim();

				if ( line.length() > 0 )
				{
					parse_line(line);
				}
			}
		}
		catch( IOException e )
		{
			throw new IOException();
		}
		finally
		{
			if ( file_stream != null )
			{
				file_stream.close();
			}
		}
	}

	private void parse_line(String line)
	{
		char	first_char = line.charAt(0);

		if ( first_char < 0xa4 && first_char != 0xa1 )
		{
			return;
		}

		line	= line.replace('/',' ');
		line	= line.replace(',',' ');
		line	= line.replace('\t',' ');

		StringTokenizer	tokenizer	= new StringTokenizer(line, " ");
		int				count		= tokenizer.countTokens();
		String			yomi		= (count > 0) ? tokenizer.nextToken() : "";
		int				yomi_len	= yomi.length();
		char			tail		= yomi.charAt(yomi_len - 1);

		if ( (tail > 0x40 && tail < 0x5b) || (tail > 0x60 && tail < 0x7b) )
		{
			yomi_len	= yomi_len - 1;
			yomi		= yomi.substring(0, yomi_len);
		}
		else
		{
			tail	= ' ';
		}

		if ( count > 1 )
		{
			while ( tokenizer.hasMoreTokens() )
			{
				String		kanji	= tokenizer.nextToken();
				if ( yomi_len > 1 && kanji.length() > 1 )
				{
					add_entry(yomi, kanji, tail);
				}
			}
		}
	}

	private void add_entry(String yomi, String kanji, char tail)
	{
		ArrayList	value_list;
		if ( kanji.charAt(0) < 0xb0 )
		{
			return;
		}

		int kanji_len	= kanji.length();

		for ( int k = 0; k < kanji_len - 1; k += 2 )
		{
           	char	kanji_first_char;
			char	kanji_second_char;

			kanji_first_char	= kanji.charAt(k);
			kanji_second_char	= kanji.charAt(k + 1);

			if (kanji_first_char < 0xa0 || kanji_second_char < 0xa0)
			{
				return;
			}

			// Need to add code for kanji nomalization at here.
			// normalize_itaiji(first_char, second_char);
		}

		for ( int j = 0; j < yomi.length() - 1; j += 2 )
		{
			char	yomi_first_char;
			char	yomi_second_char;

			yomi_first_char		= yomi.charAt(j);
			yomi_second_char	= yomi.charAt(j + 1);

			if (yomi_first_char < 0xa1)
			{
				return;
			}

			if (yomi_first_char == 0xa5)
			{
				yomi_first_char = 0xa4;
			}

			if ( yomi_first_char != 0xa4 && ( yomi_first_char != 0xa1 || yomi_second_char != 0xbc ) )
			{
				return;
			}
		}

		char	first_char		= kanji.charAt(0);
		char	second_char		= kanji.charAt(1);

		kanwa_key			key		= new kanwa_key(first_char, second_char);
		yomi_kanji_data		value	= new yomi_kanji_data(yomi, tail, kanji);

		if ( kanwa_map.containsKey(key) )
		{
			value_list	= (ArrayList)kanwa_map.get(key);
		}
		else
		{
			value_list = new ArrayList();
		}

//		This is too slow. There is no harm having duplicates.
//		if ( !value_list.contains(value) )
//		{
//			value_list.add(value);
//		}
		value_list.add(value);
		kanwa_map.put(key, value_list);
	}

	public static class kanwa_address implements Serializable
	{
		long value;
	}

	public static class kanwa_key implements Serializable
	{
		public kanwa_key(char first, char second)
		{
			key_mbr[0]	= first;
			key_mbr[1]	= second;
		}

		public boolean equals(Object object)
		{
			kanwa_key	this_key	= (kanwa_key)object;
			return ( this.key_mbr[0] == this_key.key_mbr[0] && this.key_mbr[1] == this_key.key_mbr[1] );
		}

		public int hashCode()
		{
			return	( ( key_mbr[0] << 8 ) | key_mbr[1] );
		}

		private char[] key_mbr	= new char[2];
	}

	public static class yomi_kanji_data  implements Serializable
	{
		public yomi_kanji_data(String yomi, char tail, String kanji)
		{
			yomi_mbr	= yomi;
			tail_mbr	= tail;
			kanji_mbr	= kanji;
		}

		public String	get_yomi()
		{
			return	yomi_mbr;
		}

		public String	get_kanji()
		{
			return	kanji_mbr;
		}

		public int		get_length()
		{
			return	kanji_mbr.length();
		}

		public char		get_tail()
		{
			return	tail_mbr;
		}

		public boolean	equals(Object o)
		{
			yomi_kanji_data		data	= (yomi_kanji_data)o;
			return	( yomi_mbr.equals(data.get_yomi()) && tail_mbr == data.get_tail() && kanji_mbr.equals(data.get_kanji()) );
		}

		private String	yomi_mbr;
		private char	tail_mbr;
		private String	kanji_mbr;
	}

	private	HashMap		kanwa_map		= new HashMap();
	private	HashMap		kanwa_index		= new HashMap();
}

/*
 * $History: $
 */
