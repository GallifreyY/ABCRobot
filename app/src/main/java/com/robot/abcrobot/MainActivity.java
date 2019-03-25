package com.robot.abcrobot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks
{

    private Button btn_EvaluatedFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QiSDK.register(this,this);

        btn_EvaluatedFilter = (Button) findViewById(R.id.btn_EvaluatedFilter);
        btn_EvaluatedFilterOnClick();
    }

    public void btn_EvaluatedFilterOnClick(){
        btn_EvaluatedFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent evaluatedFilterIntent = new Intent(MainActivity.this,EvaluatedFilterActivity.class);

                startActivity(evaluatedFilterIntent);
            }
        });
    }


    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Say say = (Say) SayBuilder.with(qiContext)
                .withText("Hello everybody")
                .build();
        say.run();
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    protected void onDestroy()
    {
        QiSDK.unregister(this,this);
        super.onDestroy();
    }
}