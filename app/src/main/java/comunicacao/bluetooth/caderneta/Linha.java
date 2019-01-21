package comunicacao.bluetooth.caderneta;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Enoque on 29/11/2017.
 */

public class Linha extends GraphicOverlay.Graphic {

    private Paint pLinha;
    private GraphicOverlay overlay;
    private int cor = 0;
    private static final int OPCOES_DE_COR[] = {

            Color.RED,
            Color.rgb(248, 66, 16),
            Color.rgb(248, 138, 16)
    };


    public Linha (GraphicOverlay overlay){
        super(overlay);

        pLinha = new Paint();
        pLinha.setStyle(Paint.Style.STROKE);
        pLinha.setStrokeWidth(4.0f);
        this.overlay = overlay;
    }


    @Override
    public void draw(Canvas canvas) {

        cor = (cor + 1) % OPCOES_DE_COR.length;
        final int corSelecionada = OPCOES_DE_COR[cor];
        pLinha.setColor(corSelecionada);
        canvas.drawLine(0, overlay.getHeight()/2,overlay.getWidth(),overlay.getHeight()/2, pLinha);

    }
}
