package commonClass;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pw.horand.gohome.R;

/**
 * Created by Administrator on 2016/4/18.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    int layoutRes;//布局文件
    Context context;
    private Button   btnok,btncancle;
    private LeaveMyDialogListener listener;

    public interface LeaveMyDialogListener{
        public void onClick(View view);
    }
    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }
    /**
     * 自定义布局的构造方法
     * @param context
     * @param resLayout
     */
    public CustomDialog(Context context,int resLayout){
        super(context);
        this.context = context;
        this.layoutRes=resLayout;
    }
    /**
     * 自定义主题及布局的构造方法
     * @param context
     * @param theme
     * @param resLayout
     */
    public CustomDialog(Context context, int theme,int resLayout ,LeaveMyDialogListener listener){
        super(context, theme);
        this.context = context;
        this.layoutRes=resLayout;
        this.listener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        btncancle = (Button)findViewById(R.id.mycancel);
        btnok     = (Button)findViewById(R.id.myok);
        btnok.setOnClickListener(this);
        btncancle.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        listener.onClick(v);
    }
}