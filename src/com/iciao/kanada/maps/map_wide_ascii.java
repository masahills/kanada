package com.iciao.kanada.maps;

import com.iciao.kanada.j_mapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author	Masahiko Sato
 */
public class map_wide_ascii	extends j_mapper
{
	public	map_wide_ascii()
	{
		this(0, null);
	}

	protected	map_wide_ascii(int count, String str)
	{
		super(count, str);
	}

	protected void	process(String str, int param)
	{
		int				i	= 0;
		StringBuffer	out	= new StringBuffer();

		out.append((char)(str.charAt(1) - 0x80));

		if ( out.length() == 0 )
		{
			out.append('?');
		}

		i = 2;

		set_int(i);
		set_string(out.toString());
	}
}

/*
 * $History: $
 */
