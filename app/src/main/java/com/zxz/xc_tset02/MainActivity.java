package com.zxz.xc_tset02;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private MqttConnectOptions options;
    private String host = "tcp://xb974b8b.ala.dedicated.aliyun.emqxcloud.cn";
    private String userName = "xz96881";
    private String passWord = "xz96881.";
    private String mqtt_id = "17722179396";
    private String mqtt_sub_topic = "android_test/01";
    private Handler handler;
    private MqttClient client;

    private ScheduledExecutorService scheduler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity","onCreate execute");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Mqtt_init();
        startReconnect();
        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1: //开机校验更新回传
                        break;
                    case 2:  // 反馈回传

                        break;
                    case 3:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();
                        //temp_text_view.setText(msg.obj.toString());
                        break;
                    case 4:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();
                        //humi_text_view.setText(msg.obj.toString());
                        break;
                    case 5:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();
                        //lightness_text_view.setText(msg.obj.toString());
                        break;
                    case 6:  //MQTT 收到消息回传   UTF8Buffer msg=new UTF8Buffer(object.toString());
                        //Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();
                        //gas_text_view.setText(msg.obj.toString());
                        break;
                    case 7:
                        Toast.makeText(MainActivity.this,msg.obj.toString() ,Toast.LENGTH_SHORT).show();
                        break;
                    case 30:  //连接失败
                        Toast.makeText(MainActivity.this,"连接失败" ,Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //连接成功
                        Toast.makeText(MainActivity.this,"连接成功" ,Toast.LENGTH_SHORT).show();
                        try {
                            //订阅测试主题
                            client.subscribe(mqtt_sub_topic,1);
                            //订阅温度数据
                            client.subscribe("temp",1);
                            //订阅湿度数据
                            client.subscribe("humi",1);
                            //订阅亮度数据
                            client.subscribe("lightness",1);
                            //订阅气体数据
                            client.subscribe("gas",1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };


    }

    //Mqtt初始化函数
    private void Mqtt_init()
    {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id, new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    //startReconnect();
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    if(topicName.equals("temp"))
                    {
                        //收到温度数据
                        msg.what = 3;   //收到消息标志位
                        msg.obj = message.toString() + " ℃";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if (topicName.equals("humi"))
                    {
                        //收到湿度数据
                        msg.what = 4;   //收到消息标志位
                        msg.obj = message.toString() + " %RPH";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if(topicName.equals("lightness"))
                    {
                        //收到亮度数据
                        msg.what = 5;   //收到消息标志位
                        msg.obj = message.toString() + " Lux";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if(topicName.equals("gas"))
                    {
                        //收到气体数据
                        msg.what = 6;   //收到消息标志位
                        msg.obj = message.toString() + " %";
                        handler.sendMessage(msg);    // hander 回传
                    }
                    else if(topicName.equals(mqtt_sub_topic))
                    {
                        //收到测试消息
                        msg.what = 7;   //收到测试消息标志位
                        msg.obj = message.toString() + " test";
                        handler.sendMessage(msg);    // hander 回传
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(30);
        }
    }
    //MQTT连接EMQ-X函数
    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!(client.isConnected()) )  //如果还未连接
                    {
                        client.connect(options);
                        handler.sendEmptyMessage(31); // 连接成功
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(30); // 连接失败
                }
            }
        }).start();
    }

    //MQTT重连
    //MQTT重连
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (client != null && !client.isConnected()) { // ✅ 检查空指针
                    Mqtt_connect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(30); // 发送连接失败消息
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    //MQTT发布消息
    private void publishmessageplus(String topic,String message2)
    {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }





}