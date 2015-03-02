package com.zuzhili.bussiness.utility;

import java.io.UnsupportedEncodingException;

public class String2Alpha {
    /**
     * 汉字拼音的首字母,不存在i,u,v
     */
    private final static int[] HanZiCode = {0xB0A1, 0xB0C5, 0xB2C1, 0xB4EE,
            0xB6EA, 0xB7A2, 0xB8C1, 0xB9FE, 0xBBF7, 0xBFA6, 0xC0AC, 0xC2E8,
            0xC4C3, 0xC5B6, 0xC5BE, 0xC6DA, 0xC8BB, 0xC8F6, 0xCBFA, 0xCDDA,
            0xCEF4, 0xD1B9, 0xD4D1, 0xD8A0};
    private final static int LENGTH = HanZiCode.length;

    /**
     * 该方法用于获得传入的汉字的首字母
     * 如果传入的word不属于GB2312所包含的汉字，则原样返回
     * 如果传入多个汉字，则原样返回
     *
     * @param word
     */
    public String getPinYin(String word) {
        byte[] byte1;
        char c = 'a' - 1;
        try {
            byte1 = word.getBytes("gb2312");
            if (byte1.length == 2) {
                int codeValue = ((byte1[0] + 256) * 256 + byte1[1] + 256);
                if (codeValue >= HanZiCode[0]
                        && codeValue <= HanZiCode[LENGTH - 1]) {
                    for (int i = 0; i < LENGTH; i++) {
                        if (codeValue >= HanZiCode[i]) {
                            if ((c + 1 == 'i')) {
                                c += 2;
                            } else if (c + 1 == 'u') {
                                c += 3;
                            } else {
                                c++;
                            }
                        }
                    }
                    return c + "";
                }
            }
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return word;
    }

    /**
     * 获得传入字符串中每个汉字拼音的首字母
     */
    public String chinese2PinYin(String str) {
        String result = "";
        if (str != null) {

            for (int i = 0; i < 1; i++) {
                result += getPinYin(str.substring(i, i + 1));
            }
        }
        return result;
    }


}
