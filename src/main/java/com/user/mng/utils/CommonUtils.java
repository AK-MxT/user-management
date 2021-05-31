package com.user.mng.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * フォーマットとかデータ加工系
 * 増えてきたらクラス分ける
 */
public final class CommonUtils {

	/**
	 * Date型の日付をフォーマットする
	 *
	 * @param date
	 * @return フォーマット後の日付文字列（yyyy/MM/dd HH:mm:ss）
	 */
	public static String formatToDateTime (Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String result = df.format(date);

		return result;
	}

	/**
	 * Date型の日付をフォーマットする
	 *
	 * @param date
	 * @return フォーマット後の日付文字列（yyyy/MM/dd）
	 */
	public static String formatToDate (Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		String result = df.format(date);

		return result;
	}

	/**
	 * 文字列の日付（yyyy/mm/dd）をフォーマットする
	 *
	 * @param strDate
	 * @return Date型
	 */
	public static Date formatStrToDate (String strDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

		Date date = new Date();
		try {
			date = df.parse(strDate);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return date;
	}

	public static String formatAddress (String prefecture, String add1, String add2, String add3, String add4) {

		if (StringUtils.isEmpty(add3)) {
			// 住所3が未入力の場合は住所2まで結合して返却
			return prefecture + " " + add1 + " " + add2;
		}

		String address4 = StringUtils.isEmpty(add4) ? "" : " " + add4;

		return prefecture + " " + add1 + " " + add2 + " " + add3 + address4;
	}
}
