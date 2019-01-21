package comunicacao.bluetooth.caderneta;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.images.Size;

import java.io.IOException;

import static android.R.attr.left;
import static android.R.attr.right;


/**
 * Created by Enoque on 06/11/2017.
 */

public class Superficie extends ViewGroup{


    private AutoFitTextureView textureView;
    private int mLeftWidth;

    /** The amount of space used by children in the right gutter. */
        private int mRightWidth;

     /** These are used for computing child frames based on their gravity. */
     private final Rect mTmpContainerRect = new Rect();
     private final Rect mTmpChildRect = new Rect();


    public Superficie(Context context){

        super(context);
        addView(textureView);

    }

    public Superficie (Context context, AttributeSet attributeSet){
        this(context, attributeSet, 0);

    }

    public Superficie(Context context, AttributeSet attrs, int defStyle) {
         super(context, attrs, defStyle);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = 1100;
        int height = 720;


        //RESIZE PREVIEW IGNORING ASPECT RATIO. THIS IS ESSENTIAL.
        int newWidth = height;

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;
        // Computes height and width for potentially doing fit width.
        int childWidth = layoutWidth;
        int childHeight = layoutHeight;
        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight;
            childWidth = layoutWidth;
        }
        for (int i = 0; i < getChildCount(); ++i) {getChildAt(i).layout(0, 0, childWidth, childHeight);}

    }

}
