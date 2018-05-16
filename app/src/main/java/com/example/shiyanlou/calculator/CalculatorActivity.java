package com.example.shiyanlou.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import org.wltea.expression.ExpressionEvaluator;
public class CalculatorActivity extends Activity{
    // 界面的主要的两个控件
    private GridView mGridButtons = null;
    private EditText mEditInput = null;
    // 适配器
    private BaseAdapter mAdapter = null;



    // EditText显示的内容，mPreStr表示灰色表达式部分，要么为空，要么以换行符结尾
    private String mPreStr = "";
    // mLastStr表示显示内容的深色部分
    private String mLastStr = "";

    private String opration = "";

    private String  tempStr = "";

    private boolean isClean = false;



    // gridview的所有按钮对应的键的内容
    private final String[] mTextBtns = new String[]{
            "Back","CE","无","/",
            "7","8","9","*",
            "4","5","6","+",
            "1","2","3","-",
            "0",".","无","=",};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏，需要在setContentView之前调用
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calculator);
        // 查找控件
        mGridButtons = (GridView) findViewById(R.id.grid_buttons);
        mEditInput = (EditText) findViewById(R.id.edit_input);
        // 新建adpater对象，并给GridView设置适配器
        mAdapter = new CalculatorAdapter(this, mTextBtns);
        mGridButtons.setAdapter(mAdapter);
        // 这句话的目的是为了让EditText不能从键盘输入
        mEditInput.setKeyListener(null);
        // 新建一个自定义AdapterView.OnItemClickListener的对象，用于设置GridView每个选项按钮点击事件
        OnButtonItemClickListener listener = new OnButtonItemClickListener();
        mGridButtons.setOnItemClickListener(listener);
    }
    /**
     * 这个函数用于设置EditText的显示内容，主要是为了加上html颜色标签。
     * 所有的显示EditText内容都需要调用此函数
     */
    private void setText(String msg){

        mEditInput.setText(msg);
        mEditInput.setSelection(mEditInput.getText().length());
        // 表示获取焦点
        mEditInput.requestFocus();
    }
    /**
     * 当用户按下 = 号的时候，执行的函数
     * 用于执行当前表达式，并判断是否有错误
     */
    private void excuteExpression(){
        Object result = null;
        try{
            // 第三方包执行表达是的调用
            result = ExpressionEvaluator.evaluate(mPreStr+opration+mLastStr);
        }catch (Exception e){
            // 如果捕获到异常，表示表达式执行失败，调用setError方法显示错误信息
            //Toast.makeText(this, "表达式解析错误，请检查!", Toast.LENGTH_SHORT).show();
            setText("错误");
            return;
        }
        // 显示执行结果
        setText(doubleTrans((double)result));
        opration = "";
        mPreStr = "";
        mLastStr = "";
    }


    //如果结果是3.00 就显示3
    public static String doubleTrans(double d){
        if(Math.round(d)-d==0){
            return String.valueOf((long)d);
        }
        return String.valueOf(d);
    }

    private void clean (){
        // 需要全被设置为空字符串，并设置标识为false，同时清空显示内容
        mPreStr = "";
        mLastStr = "";
        opration = "";
        mEditInput.setText("0");
    }


    /**
     * 该类是自定义选项按钮单击事件监听器
     */
    private class OnButtonItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = (String) parent.getAdapter().getItem(position);
            if(text.equals("=")){
                mLastStr = mEditInput.getText().toString();


                // 为 = 号时直接调用该方法就好
                excuteExpression();
            }
            else if(text.equals("Back")){
                if(mEditInput.getText().length() == 1){
                    if (!mEditInput.getText().toString().equals("0")){
                        setText("0");
                    }else {
                        clean();
                    }
                }else {
                    setText(mEditInput.getText().toString().substring(0,mEditInput.getText().length()-1));
                }
            }else if(text.equals("CE")){
              clean();
            }

            else if (text.equals("0") ||
                    text.equals("1") ||
                    text.equals("2") ||
                    text.equals("3") ||
                    text.equals("4") ||
                    text.equals("5") ||
                    text.equals("6") ||
                    text.equals("7") ||
                    text.equals("8") ||
                    text.equals("9") ){

                if (mEditInput.getText().toString().equals("0")){
                    setText("");
                }

                if (isClean){
                    setText("");
                    isClean = false;
                }
                setText(mEditInput.getText().toString() + text);

            }

            else if (text.equals("/") ||
                    text.equals("*") ||
                    text.equals("+") ||
                    text.equals("-") ){
                opration = text;
                mPreStr = mEditInput.getText().toString();
                isClean = true;

            }
            else if (text.equals(".")){
                if (!mEditInput.getText().toString().endsWith(".")){
                    setText(mEditInput.getText().toString() + text);
                }
            }


        }
    }
}