package com.iciao.kanada.maps;

import com.iciao.kanada.j_mapper;
import com.iciao.kanada.kanada_def;

/**
 * Remap non-kanji characters.<br>
 *
 * @author	Masahiko Sato
 */
public class map_hiragana	extends j_mapper
{
	public	map_hiragana()
	{
		this(0, null);
	}

	protected	map_hiragana(int count, String str)
	{
		super(count, str);
	}

	protected void	process(String str, int param)
	{
		int				i	= 0;
		StringBuffer	out	= new StringBuffer();

		switch( param )
		{
			case TO_KATAKANA:
			{
				char	first_char	= (char)(str.charAt(0) + 0x01);
				char	second_char	= str.charAt(1);
				out.append(first_char).append(second_char);
				i = 2;
				break;
			}
			case TO_HALF_KATAKANA:
			{
				char	first_char	= str.charAt(0);
				char	second_char	= str.charAt(1);
				out.append(first_char).append(second_char);
				i = 2;
				break;
			}

			case TO_ASCII:
			case TO_WIDE_ASCII:
			{
//				Iterator	iterater	= hiragana_to_romaji_map.keySet().iterator();
//				String	romaji	= "?";
//				int matched_len = 0;
//				while ( iterater.hasNext() )
//				{
//					String	key		= (String)iterater.next();
//					int		key_len	= key.length();
//					if ( key_len > str.length() )
//					{
//						continue;
//					}
//					String	kana	= str.substring(0, key_len);
//
//					if ( key.equals(kana) && matched_len < key_len )
//					{
//						romaji	= (String)kanada_def.hiragana_to_romaji_map.get(key);
//						matched_len		= key_len;
//					}
//				}
//				i = matched_len > 0 ? matched_len : 2;

				i = str.length() > 8 ? 8 : str.length();
				if ( i % 2 == 1 )
				{
					--i;
				}
				String	romaji	= "";
				while( i > 1 )
				{
					String	kana	= str.substring(0, i);
					if ( kanada_def.hiragana_to_romaji_map.containsKey(kana) )
					{
						romaji	= (String)kanada_def.hiragana_to_romaji_map.get(kana);
						break;
					}
					i = i - 2;
				}
				out.append(romaji);
				break;
			}
			default:
			{
				char	first_char	= str.charAt(0);
				char	second_char	= str.charAt(1);
				out.append(first_char).append(second_char);
				i = 2;
				break;
			}
		}

		set_int(i);
		set_string(out.toString());
	}
}

/*
 * $History: $
 */
