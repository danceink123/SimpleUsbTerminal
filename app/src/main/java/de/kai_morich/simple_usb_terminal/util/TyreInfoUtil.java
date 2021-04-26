package de.kai_morich.simple_usb_terminal.util;

import android.text.SpannableStringBuilder;

import de.kai_morich.simple_usb_terminal.bean.TyreInfoBean;

public class TyreInfoUtil {
    private byte defaultTypeValue = -17;

    private SpannableStringBuilder dumpTyreInfo(TyreInfoBean tyreInfoBean) {
        SpannableStringBuilder spn = new SpannableStringBuilder();

        String tyreName = "";
        if (tyreInfoBean.getPosition() == 2) {
            tyreName = "左前轮";
        } else if (tyreInfoBean.getPosition() == 1) {
            tyreName = "右前轮";
        } else if (tyreInfoBean.getPosition() == 4) {
            tyreName = "左后轮";
        } else if (tyreInfoBean.getPosition() == 3) {
            tyreName = "右后轮";
        } else {
            //postion为5时数据未知
            tyreName = String.valueOf(tyreInfoBean.getPosition());
        }
        if (tyreInfoBean.getPosition()<5) {
            spn.append(tyreName).append("--").append(tyreInfoBean.getSensorId()).append("，压力：").append(tyreInfoBean.getAirPressure()).append("，温度：").append(tyreInfoBean.getTemperature()).append("，压力值：").append(String.valueOf(tyreInfoBean.getAirValue())).append("\n");
            return spn;
        }
        return spn;
    }

    public static TyreInfoBean handleReceiveData(byte[] bArr) {
        byte f2 = getTypeByte(bArr);
        if (f2 != 17) {
            switch (f2) {
                case 98:
                    return null;
                case 99:
                    return handleTyreByteArr(bArr);
                default:
                    switch (f2) {
                        case 101:
                        case 102:
                        case 103:
                        default:
                            return null;
                    }
            }
        }
        return null;
    }

    private static byte getTypeByte(byte[] data){
        if (data == null || data.length < 6) {
            return -17;
        }
        return data[4];
    }

    private static TyreInfoBean handleTyreByteArr(byte[] bArr) {
        TyreInfoBean tyreInfoBean = null;
        try {
            byte[] bArr2 = new byte[8];
            if (bArr != null && bArr[4] == 99 && bArr.length >= 7) {
                if (bArr[5] == 0) {
                    if (bArr.length >= 15) {
                        System.arraycopy(bArr, 6, bArr2, 0, bArr2.length);
                        tyreInfoBean = getTyreInfoBean(bArr2);
                    }
                } else if (bArr[5] != -1 && bArr[5] != -86 && bArr.length >= 14) {
                    System.arraycopy(bArr, 5, bArr2, 0, bArr2.length);
                    tyreInfoBean = getTyreInfoBean(bArr2);//                    b((mate.steel.com.t620.usb.a<Boolean>) null);
                }
            }
            return tyreInfoBean;
        } catch (Exception e) {
            return tyreInfoBean;
        }

    }

    private void postTyreInfo(byte[] bArr) {
        synchronized (this) {
            if (bArr != null) {
                if (bArr.length == 8) {
                    TyreInfoBean g2 = getTyreInfoBean(bArr);
                }
            }
        }
    }

    private static TyreInfoBean getTyreInfoBean(byte[] bArr) {
        if (bArr == null || bArr.length != 8) {
            return null;
        }
        TyreInfoBean tyreInfoBean = new TyreInfoBean();
        StringBuilder sb = new StringBuilder();
        tyreInfoBean.setPosition(bArr[0] & 255);// 0-3对应右前，左前，右后，左后
        sb.append("0x");
        sb.append(HexDump.a(bArr[1]));
        sb.append(HexDump.a(bArr[2]));
        sb.append(HexDump.a(bArr[3]));
        tyreInfoBean.setSensorId(sb.toString());
        float parseFloat = Float.parseFloat(HexDump.getBigDecimalStr(((float) HexDump.getBigDecimalDou(((bArr[4] << 8) & 768) | (bArr[5] & 255), 0.025d)) + "", 1));
        tyreInfoBean.setAirValue(parseFloat);
        tyreInfoBean.setAirPressure(HexDump.getBigDecimalStr(parseFloat + "", 1));
        tyreInfoBean.setTemperature("" + ((bArr[6] & 255) + -50));
        tyreInfoBean.setState(bArr[7]);
//        dumpTyreInfo(tyreInfoBean);
        return tyreInfoBean;
    }
}
