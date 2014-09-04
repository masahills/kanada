package com.iciao.kanada;

/**
 * Remap non-kanji characters.<br>
 *
 * @author	Masahiko Sato
 */
public abstract class j_mapper	extends kanada_def
{
	protected int 		count_mbr;
	protected String	out_str_mbr;

	protected	j_mapper(int count, String str)
	{
		count_mbr	= count;
		out_str_mbr	= str;
	}

	protected int get_int()
	{
		return	count_mbr;
	}

	protected String get_string()
	{
		return	out_str_mbr;
	}

	protected void set_int(int count)
	{
		count_mbr	= count;
	}

	protected void set_string(String str)
	{
		out_str_mbr	= str;
	}

	protected void append(String str)
	{
		StringBuffer	buf	= new StringBuffer();
		buf.append(str);
		out_str_mbr	= buf.toString();
	}

	protected abstract void	process(String str, int param);
}

/*
 * $History: $
 */
