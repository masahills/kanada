package com.iciao.kanada.maps;

import com.iciao.kanada.j_mapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author	Masahiko Sato
 */
public class map_half_katakana	extends j_mapper
{
	public	map_half_katakana()
	{
		this(0, null);
	}

	protected	map_half_katakana(int count, String str)
	{
		super(count, str);
	}

	protected void	process(String str, int param)
	{
		int				i	= 0;
		StringBuffer	out	= new StringBuffer();

		char	first_char	= str.charAt(0);
		out.append(first_char);
		i = 1;

		set_int(i);
		set_string(out.toString());
	}
}

/*
 * $History: $
 */
