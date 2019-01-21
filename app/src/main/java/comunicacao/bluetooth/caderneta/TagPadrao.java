package comunicacao.bluetooth.caderneta;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Enoque on 19/10/2017.
 */

public class TagPadrao extends android.support.v7.widget.AppCompatTextView {


    public TagPadrao(Context context) {
        super(context);
        this.setWidth(340);
        this.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.setTextSize(16);
    }
}
