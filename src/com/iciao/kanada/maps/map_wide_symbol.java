package com.iciao.kanada.maps;

import com.iciao.kanada.j_mapper;

/**
 * Remap non-kanji characters.<br>
 *
 * @author	Masahiko Sato
 */
public class map_wide_symbol	extends j_mapper
{
	public	map_wide_symbol()
	{
		this(0, null);
	}

	protected	map_wide_symbol(int count, String str)
	{
		super(count, str);
	}

	protected void	process(String str, int param)
	{
		int				i	= 0;
		StringBuffer	out	= new StringBuffer();

		char	first_char	= str.charAt(0);

		try
		{
			char	second_char = str.charAt(1);

			if ( second_char < 0xa1 )
			{
				// Out of range. Not a Japanese character.
				out.append(first_char);
				out.append(second_char);
				i = 2;
			}

			switch( first_char )
			{
				case 0xa1:
				{
					out.append(wide_symbol_a1_to_ascii[second_char - 0xa1]);
					i = 2;
					break;
				}
				case 0xa2:
				{
					out.append(wide_symbol_a2_to_ascii[second_char - 0xa1]);
					i = 2;
					break;
				}
				case 0xa3:
				{
					if ( second_char < 0x80 )
					{
						// Out of range. Not a Japanese character.
						out.append(first_char);
						out.append(second_char);
						i = 2;
					}
					else
					{
						out.append((char)(second_char - 0x80));
						i = 2;
					}
					break;
				}
				case 0xa6:
				{
					out.append(wide_symbol_a6_to_ascii[second_char - 0xa1]);
					i = 2;
					break;
				}
				default:
				{
					// You sould never get here.
					out.append(first_char);
					out.append(second_char);
					i = 2;
					break;
				}
			}
		}
		// ArrayIndexOutOfBoundsException or StringIndexOutOfBoundsException may occur
		// while processing non-Japanese double byte texts or other non-Ascii chars.
		catch ( Exception e )
		{
			out.append(first_char);
			i = 1;
		}

		set_int(i);
		set_string(out.toString());
	}
}

/*
 * $History: $
 */
