package com.ihesen.autosendmsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.ihesen.autosendmsg.MainActivity.EMAIL;

/**
 * 类名
 *
 * @author hesen
 *         实现的主要功能：
 *         创建日期 2017/10/10
 *         修改者，修改日期，修改内容。
 */
public class SmsReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                Log.e("ihesen", "number:" + msg.getOriginatingAddress()
                        + " body:" + msg.getDisplayMessageBody() + "  time:"
                        + msg.getTimestampMillis());
                sendEmail(msg.getOriginatingAddress(), msg.getDisplayMessageBody(), receiveTime, context.getSharedPreferences(EMAIL, MODE_PRIVATE).getString(EMAIL, "hesen@beiquan.org"));
            }
        }
    }

    public void sendEmail(final String mobile, final String content, final String receiveTime, final String email) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    EmailSender sender = new EmailSender();
                    //设置服务器地址和端口，网上搜的到
                    sender.setProperties("smtp.163.com", "25");
                    //分别设置发件人，邮件标题和文本内容
                    sender.setMessage("发件人邮箱地址", mobile, receiveTime + "\n" + content);
                    //设置收件人
                    sender.setReceiver(new String[]{email});
                    //添加附件
                    //这个附件的路径是我手机里的啊，要发你得换成你手机里正确的路径
                    // sender.addAttachment("/sdcard/DCIM/Camera/asd.jpg");
                    //发送邮件
                    sender.sendEmail("smtp.163.com", "邮箱地址", "邮箱密码");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
