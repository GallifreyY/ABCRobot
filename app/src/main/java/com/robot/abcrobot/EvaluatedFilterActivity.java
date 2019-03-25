package com.robot.abcrobot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.robot.abcrobot.dbmodel.ChildInfo;
import com.robot.abcrobot.dbmodel.Question;
import com.robot.abcrobot.dbmodel.ScreeningQuestionnaire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class EvaluatedFilterActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private Button btn_StartAssessing;

    private TextView textView_QuestionView;
    private int currentQuestionIndex;
    private int resultCount;

    public List<Question> questionCollection;
    public ChildInfo assessed_ChildInfo;

    private MobileServiceClient mClient;


    // Chind info layout
    private Spinner spinner_Sex, spinner_Preparer;
    private EditText editText_Child_Name, editText_Child_Age;
    private Button btn_StartAnswering;

    // Answer questions layout
    private Button btn_PreQuestion;
    private Button btn_NextQuestion;
    private Button btn_AnswerYes;
    private Button btn_AnswerNo;
    private Button btn_SubmitResult;
    private GridLayout gridLayout_AnswerYesNo;
    private RadioGroup radioGroup_QuestionLevel;
    private ProgressBar mProgressBar;

    // Assessed result layout
    private TextView textView_Result_ChildName,textView_Result_ChildSex,textView_Result_ChildAge,textView_Result_ChildAssessedDate,textView_Result_ChildScore,textView_Result_Analysis;
    private Button btn_End_Assessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluated_filter);

        currentQuestionIndex =-1;
        btn_StartAssessing = (Button)findViewById(R.id.btn_StartAssessing);

        AzureServiceAdapter.Initialize(this);

        mClient  = AzureServiceAdapter.getInstance().getClient();

        LoadQuestions();
        btn_StartAssessingOnClick();
    }



    public void LoadQuestions() {
        questionCollection = new ArrayList<Question>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.questiondata);
            InputStreamReader isr = new InputStreamReader(inputStream,"UTF-8");

            BufferedReader reader = new BufferedReader(isr);
            String line;
            int countLine=0;
            while ((line = reader.readLine()) != null) {
                countLine++;
                questionCollection.add(new Question("Q"+Integer.toString(countLine)+". "+line));
                if(countLine==10)
                {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btn_StartAssessingOnClick(){
        btn_StartAssessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.activity_chindinfo);
                InitialFillChildInfoView();
            }
        });
    }

//////////////////////////////////////////////////////////////////////////////
    // Child info layout view
    private void InitialFillChildInfoView()
    {
        assessed_ChildInfo = new ChildInfo();
        editText_Child_Name = (EditText)findViewById(R.id.txt_Child_Name);
        editText_Child_Age = (EditText)findViewById(R.id.txt_Child_Age);

        editText_Child_Name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); imm.hideSoftInputFromWindow(editText_Child_Name.getWindowToken(), 0);
                return false;
            }
        });

        editText_Child_Age.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); imm.hideSoftInputFromWindow(editText_Child_Name.getWindowToken(), 0);
                return false;
            }
        });

        spinner_Sex = (Spinner)findViewById(R.id.spinner_Child_Sex);
        spinner_Preparer = (Spinner)findViewById(R.id.spinner_Child_Preparer);

        spinner_Sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               updateSpinnerTextSize(view);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinner_Preparer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSpinnerTextSize(view);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        assessed_ChildInfo.setAssessedDate(new Date(System.currentTimeMillis()));

        btn_StartAnswering= (Button)findViewById(R.id.btn_StartAnswering);
        btn_StartAnswering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(editText_Child_Name.getText().toString().trim().isEmpty() ) {
//                    Toast alertToast = Toast.makeText(EvaluatedFilterActivity.this,R.string.alert_ChildNameIsEmpty_String,Toast.LENGTH_LONG);
//                    alertToast.setGravity(Gravity.CENTER,0,0);
//                    alertToast.show();
//                }
                if( editText_Child_Age.getText().toString().trim().isEmpty())
                {
                    Toast alertToast = Toast.makeText(EvaluatedFilterActivity.this,R.string.alert_ChildBirthdayError_String,Toast.LENGTH_LONG);
                    alertToast.setGravity(Gravity.CENTER,0,0);
                    alertToast.show();
                }
                else
                {
                    FillChildBasedInfo();
                    setContentView(R.layout.activity_answer_questions);
                    InitialAnswerQuestionLayout();
                    currentQuestionIndex = 0;
                    textView_QuestionView.setText(questionCollection.get(currentQuestionIndex).getTitle());
                }

            }
        });
    }

    private void updateSpinnerTextSize(View view)
    {
        TextView tv = (TextView)view;
        tv.setTextSize(35.0f);    //设置大小
    }

    private void FillChildBasedInfo()
    {
        assessed_ChildInfo.setName(editText_Child_Name.getText().toString().trim());
        assessed_ChildInfo.setAge(Integer.parseInt(editText_Child_Age.getText().toString().trim()));
        assessed_ChildInfo.setPreparer(spinner_Preparer.getSelectedItem().toString());
        assessed_ChildInfo.setSex(spinner_Sex.getSelectedItem().toString());
    }

/////////////////////////////////////////////////////////////////////////
    // Answer question layout logic
    private void InitialAnswerQuestionLayout()
    {
        btn_PreQuestion=(Button)findViewById(R.id.btn_PreQuestion);
        btn_NextQuestion=(Button)findViewById(R.id.btn_NextQuestion);
        btn_AnswerYes=(Button)findViewById(R.id.btn_AnswerYes);
        btn_AnswerNo=(Button)findViewById(R.id.btn_AnswerNo);
        btn_SubmitResult= (Button)findViewById(R.id.btn_SubmitResult);
        textView_QuestionView = (TextView)findViewById(R.id.text_Question);
        gridLayout_AnswerYesNo = (GridLayout)findViewById(R.id.gl_AnswerYesNoContainer);
        radioGroup_QuestionLevel = (RadioGroup)findViewById(R.id.rg_AnswerQuestionLevel);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar_Submitting);
        mProgressBar.setVisibility(ProgressBar.GONE);

        gridLayout_AnswerYesNo.setVisibility(View.VISIBLE);
        radioGroup_QuestionLevel.setVisibility(View.INVISIBLE);

        btn_PreQuestionOnClick();
        btn_NextQuestionOnClick();
        btn_AnswerYesOnClick();
        btn_AnswerNoOnClick();
        btn_SubmitResultOnClick();
        radioGroup_QuestionLevel.setOnCheckedChangeListener(this::onCheckedChanged);
    }

    public void btn_PreQuestionOnClick() {
        btn_PreQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > -1) {
                    currentQuestionIndex--;
                    UpdateQuestion();
                }
            }
        });
    }

    public void btn_NextQuestionOnClick() {
        btn_NextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question cq = questionCollection.get(currentQuestionIndex);
                if(cq.selectedAnswer==-1) {
                    Toast alertToast = Toast.makeText(EvaluatedFilterActivity.this, R.string.alert_CurrentQuestionNoAnswer_String, Toast.LENGTH_LONG);
                    alertToast.setGravity(Gravity.CENTER, 0, 0);
                    alertToast.show();
                    return;
                }
                else if(cq.selectedAnswer==1&&cq.scoreLevel==0) {
                    Toast alertToast = Toast.makeText(EvaluatedFilterActivity.this, R.string.alert_CurrentQuestionLevel_String, Toast.LENGTH_LONG);
                    alertToast.setGravity(Gravity.CENTER, 0, 0);
                    alertToast.show();
                    return;
                }

                if (currentQuestionIndex < questionCollection.size() - 1) {
                    currentQuestionIndex++;
                    UpdateQuestion();

                }

                UpdateOperatedButtonStates();
            }
        });
    }

    public void btn_AnswerYesOnClick() {
        btn_AnswerYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > -1 && questionCollection.size() > 0) {
                    if( questionCollection.get(currentQuestionIndex).selectedAnswer == -1)
                    {
                        resultCount++;
                    }

                    questionCollection.get(currentQuestionIndex).selectedAnswer = 1;
                    UpdateQuestionResultUIVisibility(1);
                    btn_AnswerYes.setEnabled(false);
                    btn_AnswerNo.setEnabled(true);
                }
            }
        });
    }

    public void btn_AnswerNoOnClick() {
        btn_AnswerNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex > -1 && questionCollection.size() > 0) {
                    if(questionCollection.get(currentQuestionIndex).selectedAnswer == -1)
                    {
                        resultCount++;
                    }

                    questionCollection.get(currentQuestionIndex).selectedAnswer = 0;
                    questionCollection.get(currentQuestionIndex).scoreLevel=0;
                    UpdateQuestionResultUIVisibility(0);
                    radioGroup_QuestionLevel.clearCheck();
                    btn_AnswerNo.setEnabled(false);
                    btn_AnswerYes.setEnabled(true);
                    updateSubmitButtonState();
                }
            }
        });
    }

    public void btn_SubmitResultOnClick(){
        btn_SubmitResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Question q: questionCollection) {
                    assessed_ChildInfo.setAssessedScore(assessed_ChildInfo.getAssessedScore() + q.scoreLevel);
                }

                UploadDataIntoAzureDB();

                setContentView(R.layout.activity_assessed_result);
                InitialAssessedResultLayout();

            }
        });
    }

    private void UploadDataIntoAzureDB() {
        MobileServiceTable<ChildInfo> cTable = mClient.getTable(ChildInfo.class);
        MobileServiceTable<ScreeningQuestionnaire> sqTable = mClient.getTable(ScreeningQuestionnaire.class);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final ChildInfo entity = cTable.insert(assessed_ChildInfo).get();
                    for (Question q: questionCollection) {
                        sqTable.insert(new ScreeningQuestionnaire(entity.getID(),q.getTitle(),q.scoreLevel));
                    }
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);
    }

    private  void UpdateQuestion() {
        textView_QuestionView.setText(questionCollection.get(currentQuestionIndex).getTitle());
    }

    private void UpdateOperatedButtonStates()
    {
        Question currentQuestion = questionCollection.get(currentQuestionIndex);

        UpdateQuestionResultUIVisibility(currentQuestion.selectedAnswer);

        if (currentQuestion.selectedAnswer != -1) {

            btn_AnswerNo.setEnabled(currentQuestion.selectedAnswer == 1 ? true : false);
            btn_AnswerYes.setEnabled(currentQuestion.selectedAnswer == 1 ? false : true);
            if (currentQuestion.selectedAnswer == 1) {
                switch (currentQuestion.scoreLevel) {
                    case 1:
                        radioGroup_QuestionLevel.check(R.id.radioButton_Question_Light);
                        break;
                    case 2:
                        radioGroup_QuestionLevel.check(R.id.radioButton_Question_Medium);
                        break;
                    case 3:
                        radioGroup_QuestionLevel.check(R.id.radioButton_Question_Heavy);
                        break;
                    case 4:
                        radioGroup_QuestionLevel.check(R.id.radioButton_Question_Serious);
                        break;
                }
                //(RadioButton)findViewById(radioGroup_QuestionLevel.getCheckedRadioButtonId())
            }

        }
        else {
            radioGroup_QuestionLevel.clearCheck();
            btn_AnswerNo.setEnabled(true);
            btn_AnswerYes.setEnabled(true);
        }

        btn_PreQuestion.setEnabled(currentQuestionIndex == 0 ? false : true);
        btn_NextQuestion.setEnabled(currentQuestionIndex == questionCollection.size()-1 ? false : true);

    }


    private void UpdateQuestionResultUIVisibility(int showType)
    {
        //gridLayout_AnswerYesNo.setVisibility(showType==1?View.INVISIBLE:View.VISIBLE);
        radioGroup_QuestionLevel.setVisibility(showType==1?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            case R.id.radioButton_Question_Light:
                questionCollection.get(currentQuestionIndex).scoreLevel = 1;
                break;
            case R.id.radioButton_Question_Medium:
                questionCollection.get(currentQuestionIndex).scoreLevel = 2;
                break;
            case R.id.radioButton_Question_Heavy:
                questionCollection.get(currentQuestionIndex).scoreLevel = 3;
                break;
            case R.id.radioButton_Question_Serious:
                questionCollection.get(currentQuestionIndex).scoreLevel = 4;
                break;
        }
        updateSubmitButtonState();
    }

    private void updateSubmitButtonState()
    {
        if(currentQuestionIndex==questionCollection.size()-1)
        {
            btn_SubmitResult.setVisibility(resultCount==questionCollection.size()?View.VISIBLE:View.INVISIBLE);
        }
    }

 /////////////////////////////////////////////////////////////////////////////////
    // Assessed result layout
    private void InitialAssessedResultLayout()
    {
        textView_Result_ChildName = (TextView)findViewById(R.id.textView_result_Child_Name);
        textView_Result_ChildSex = (TextView)findViewById(R.id.textView_result_Child_Sex);
        textView_Result_ChildAge = (TextView)findViewById(R.id.textView_result_Child_Age);
        textView_Result_ChildAssessedDate = (TextView)findViewById(R.id.textView_result_Child_AssessedDate);
        textView_Result_ChildScore = (TextView)findViewById(R.id.textView_result_Child_AssessedScore);
        textView_Result_Analysis = (TextView)findViewById(R.id.textView_result_Analysis);
        btn_End_Assessing = (Button)findViewById(R.id.btn_End_Assessing);

        textView_Result_ChildName.setText(assessed_ChildInfo.getName());
        textView_Result_ChildSex.setText(assessed_ChildInfo.getSex());
        textView_Result_ChildAge.setText(Integer.toString(assessed_ChildInfo.getAge()));

        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd");
        textView_Result_ChildAssessedDate.setText(dateFormat.format(assessed_ChildInfo.getAssessedDate()));

        textView_Result_ChildScore.setText(Integer.toString(assessed_ChildInfo.getAssessedScore()));

        AnalyzedResultScore();
        btn_EndAssessingOnClick();
    }

    private void AnalyzedResultScore()
    {
        if(assessed_ChildInfo.getAssessedScore()<53)
        {
            textView_Result_Analysis.setText(R.string.assessed_Analysis_Result_Level01);
        }
        else if(assessed_ChildInfo.getAssessedScore()<67)
        {
            textView_Result_Analysis.setText(R.string.assessed_Analysis_Result_Level02);
        }
        else if(assessed_ChildInfo.getAssessedScore()>67)
        {
            textView_Result_Analysis.setText(R.string.assessed_Analysis_Result_Level03);
        }
    }

    public void btn_EndAssessingOnClick(){
        btn_End_Assessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EvaluatedFilterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
