package comunicacao.bluetooth.caderneta;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;
import static com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT;




/*
* Esta classe cria refenências para fragemntos que embarca uma superfície que realiza a detecção de códigos
* de barra atrvés de frame enviados pela camera.
* */

public class  FragmentoParaCamera extends Fragment{

    //tag do fragmento para o controle do no LOG.
    private static final String TAG = "FragmentoParaCamera";

    //Realiza a conversão dos valores de orientação da tela para os padrões de orintação do JPEG.
    private static final SparseIntArray ORIENTACOES = new SparseIntArray();
    private static final int RC_HANDLE_GMS = 9001;

    private CameraSource cameraSource;


    //Faz a conversão dos valores.
    static {
        ORIENTACOES.append(Surface.ROTATION_0, 90);
        ORIENTACOES.append(Surface.ROTATION_90, 0);
        ORIENTACOES.append(Surface.ROTATION_180, 270);
        ORIENTACOES.append(Surface.ROTATION_270, 180);
    }


    //código para solicitação de permissão de acesso a câmera
    private static final int REQUISICAO_PERMISSAO_DA_CAMERA = 1;

    //Alguns diálogos serão apresentados para a interação com o usuário, se faz necessário a definição de uma
    //string para a identificação destes dialogos.
    private static final String FRAGMENTO_DE_DIALOGO = "dialogo";


    /*
    * O processo de acesso a câmera requer o controle dos estados que ela pode assumir durante a sua utilização.
    * Para o controlar tais estados são definidas uma série de constantes estáticas utilizadas para representar
    * cada um destes estados.
    * Para o propósito do aplicativo talvez serão utilizados apenas alguns estados.
    * */

    private static final int ESTADO_DE_PRE_VISUALIZACAO = 0;

    private static final int ESTADO_ESPERANDO_TRAVAR_O_FOCO = 1;

    private static final int ESTADO_ESPERANDO_EM_PRE_CAPTURA = 2;

    private static final int ESTADO_ESPERANDO_EM_NAO_PFECAPTURA = 3;

    private static  final int ESTADO_FOTO_TIRADA = 4;
    private Context mContext;

    //estado inicial da câmera
    private int estado = ESTADO_DE_PRE_VISUALIZACAO;

    // Eses são a altura e alrgura mázimas para a pro-visualização que são permitidas pelas API Camera2
    private static int LARGURA_MAXIMA = 1920;

    private static int ALTURA_MAXIMA = 1050;


    /*Para monitorar um possível mudança de comportamento da superfície onde serão exibidos os frames
     *enviados pela câmera, por exemplo, uma mudança da dimensões deivo a rotação da tela,
    * cria-se um listener de superfície.
     */

    private final TextureView.SurfaceTextureListener listenerParaASuperficie
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            abreCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            modificaSuperficie(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };


    //Um objeto que representa um dispositivo de câmera.
    private CameraDevice camera;

    private int mFlashMode = 0;


    //Para gerenciar as propriedades da câmera é preciso de uma instência de câmera manager.
    private CameraManager gerenciadorDaCamera;


    private  String idCamera; //String que identifica a câmera que será acessada.


    private  CameraCaptureSession sessaoDeCaptura;//Objeto que representa uma para requisições decaptura de imgens com a
                                                  //câmera.

    private Superficie superficie; //Superfície onde as imagens da câmera serão exibidas.


    private ImageReader leitorDeImagens; //Uma intância desta class fornece outra superfície para onde as imagens

    //também serão enviadas. Desta superfície, os frames serão recuperados e processados por uma Thread a parte


    private Size tamanhoDaExibição; //Guarda as dimensões da exibição da camera


    //Sinaliza quando a câmera deve ser aberta ou fecha. Evita ques estas ações sejam feitas de maneira
    //inapropriada
    private Semaphore abreEFechaACamera = new Semaphore(1);

    private SurfaceView mSurfaceView;
    /*
     * Callback para os estados associados à tentativa de acesso à câmera.
     */

    private final CameraDevice.StateCallback callbackParaOsEstadosDaCamera = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {

            // Quando a cêmera é aberta inciamos uuma sessão de captura com a câmera para inciar a previsalização.
            abreEFechaACamera.release();
            camera = cameraDevice;
            criaSessaoDePreVisualizacao();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            abreEFechaACamera.release();
            cameraDevice.close();
            camera = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            abreEFechaACamera.release();
            cameraDevice.close();
            camera = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };


    /*
     * Esta é uma thread adicional para executar em plano de tarefas que bloquariam a interface de usuário.
     */
    private HandlerThread threadParaPlanoDefundo;
    private final int CB_LIDO = 7;



    /*
    * Cria um handler para tratar as tarefas que estão executando em plano de fundo.
    */
    private Handler handlerParaPlanoDeFundo;
    private static Handler bCHandler;

    /**
     * Cria um runnable que realizará a tera de processar o frames vindos da camera para o leitor de imagens.
     * O objetivo é realizar a identificação dos códigos de barra a partir do processamento destes frames
     */
    private Thread mProcessingThread;

    private FrameProcessingRunnable mFrameProcessor;


    /*
    * Cria uma detector de ccódigos de barras.
    */
    private Detector<Barcode> detector;

    private static int frameInd =0;

    private TextureView textura;

    /*
    * Cria um callback para notificação de quando uma imagem está disponível na superfície do leitor
    * de imagens.
    */

    private final ImageReader.OnImageAvailableListener imagemDisponível
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {

            Image mImage = reader.acquireLatestImage();
            byte[] bytes;
            frameInd++;
            overlay2.add(linha);
            if(mImage == null) {

                return;

            }

            try{

                bytes = convertYUV420888ToNV21(mImage);
                mFrameProcessor.setNextFrame(bytes, mImage.getTimestamp(), frameInd, textureView.getWidth(),
                        textureView.getHeight());

            }catch (Exception e){

                Toast.makeText(getContext(), "Erro ao converter imagem", Toast.LENGTH_SHORT).show();
            }

            mImage.close();

        }

    };

    //Builder para requisicçoes de pre-visuaização
    private CaptureRequest.Builder constroiRequisicaoDePrevisualizacao;


    //Resuisição de pré-visualização
    private CaptureRequest requisicaoDePrevisualizacao;


    //Armaena a condição de portabilidade do flash pela câmera
    private boolean suportaFlash;


    //Para a orientção da câmera
    private int sensorDeOrientacao;

    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    private BarcodeDetector barcodeDetector;


    /*
    * Callback para as capturas realizadas pela câmera.
    **/

    private CameraCaptureSession.CaptureCallback callbackParaCapturas
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {

        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };
    private ImageReader leitorDeImagensContinua;

    AutoFitTextureView textureView;
    private GraphicOverlay<Linha> overlay2;
    private Linha linha;



    /*
    * Esta função recebe uma coleção de possibilidades de tamanhos para os frames de pré-visualização que
    * são permitidos pela câmera. Avalia cada um deles comparando com o tamanho da superfície e esolhendo um
    * tamanho que seja ótimo levando em consideração estes parâmetros.
    *
    */

    private static Size escolheOTamanhoOtimo(Size[] escolhas, int larguraDaSuperficie, int alturaDaSuperficie,
                                             int larguraMaxima, int alturaMaxima, Size razaoDeAspecto) {

        //Recebe os tamanhos suportados que são tão grande quanto a superfície.
        List<Size> grandeOSuficiente = new ArrayList<>();
        //Recebe os tamanhos suportados que são menosres que a superficie
        List<Size> naoGrandesOSuficiente = new ArrayList<>();
        int w = razaoDeAspecto.getWidth();
        int h = razaoDeAspecto.getHeight();
        for (Size opcao : escolhas) {
            if (opcao.getWidth() <= larguraMaxima && opcao.getHeight() <= alturaMaxima &&
                    opcao.getHeight() == opcao.getWidth() * h / w) {
                if (opcao.getWidth() >= larguraDaSuperficie &&
                        opcao.getHeight() >= alturaDaSuperficie) {
                    grandeOSuficiente.add(opcao);
                } else {
                    naoGrandesOSuficiente.add(opcao);
                }
            }
        }

        // Escolhe o menor tamanho de todos os tamanhos possíveis que são tão grandes quanto a superfície,
        // se não houver nenhum tamanho tão grande quanto então de todos que não são tão grandes quanto a
        // superfície devolva o maior.

        if (grandeOSuficiente.size() > 0) {
            return Collections.max(grandeOSuficiente, new CompareTamanhosPorArea());
        } else if (naoGrandesOSuficiente.size() > 0) {
            return Collections.max(naoGrandesOSuficiente, new CompareTamanhosPorArea());
        } else {
            Log.e(TAG, "Não foi possível encontrar um tamanho apropriado");
            return escolhas[0];
        }
    }


    //Retorna uma nova instância de FragmentoParaCamera
    public static FragmentoParaCamera novaInstancia(Handler bCHandler){
        FragmentoParaCamera.bCHandler = bCHandler;
        return new FragmentoParaCamera();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        mGraphicOverlay = (GraphicOverlay) view.findViewById(R.id.overlay);
        mGraphicOverlay.setCameraInfo(mGraphicOverlay.getWidth(), mGraphicOverlay.getHeight(), CameraSource.CAMERA_FACING_BACK);

        overlay2 = (GraphicOverlay) view.findViewById(R.id.overlay2);
        overlay2.setCameraInfo(overlay2.getWidth(), overlay2.getHeight(), CameraSource.CAMERA_FACING_BACK);
        linha = new Linha(overlay2);

        superficie = (Superficie)view.findViewById(R.id.superficieView);
        textureView = new AutoFitTextureView(getContext());
        textureView.setAspectRatio(mGraphicOverlay.getWidth(), mGraphicOverlay.getHeight());
        superficie.addView(textureView);
        textureView.setSurfaceTextureListener(mSurfaceTextureListener);

        mGraphicOverlay.bringToFront();
        overlay2.bringToFront();

        barcodeDetector = new BarcodeDetector.Builder(getContext()).setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, bCHandler);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        mFrameProcessor = new FrameProcessingRunnable(barcodeDetector);
        Thread thread = new Thread(mFrameProcessor);
        thread.start();
        mFrameProcessor.setActive(true);



    }

    @Override
    public void onResume() {
        super.onResume();
        iniciaThreadParaPlanoDeFundo();
    }

    @Override
    public void onPause() {
        fechaACamera();
        pareAThreadDePlanoDeFundo();
        super.onPause();
    }

    //Solicita ao sistemas permissão para acessar a câmera

    private void solicitaPermissaoParaCamera(){

        if (FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new DialogoDeConfirmacao().show(getChildFragmentManager(), FRAGMENTO_DE_DIALOGO);
        } else {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUISICAO_PERMISSAO_DA_CAMERA);
        }

    }

    //Avalia o resultado da solicitação da permissão.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUISICAO_PERMISSAO_DA_CAMERA) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                DialogoDeErro.newInstance(getString(R.string.request_permission))
                        .show(getChildFragmentManager(), FRAGMENTO_DE_DIALOGO);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    //Função que configura as variáveis relacionada com os membros da câmera.

    private void configuraAsSaidasDaCamera(int largura, int altura){

        Activity activity = getActivity();
        CameraManager gerenciador = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String idDaCamera : gerenciador.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = gerenciador.getCameraCharacteristics(idDaCamera);

                // A camera frontal não é utilizada.
                Integer frontal = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (frontal != null && frontal == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap mapa = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (mapa == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(mapa.getOutputSizes(ImageFormat.JPEG)),
                         new CompareTamanhosPorArea());
                leitorDeImagensContinua = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);

               leitorDeImagensContinua.setOnImageAvailableListener(imagemDisponível, handlerParaPlanoDeFundo);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                sensorDeOrientacao = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (sensorDeOrientacao == 90 || sensorDeOrientacao == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (sensorDeOrientacao == 0 || sensorDeOrientacao== 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = largura;
                int rotatedPreviewHeight = altura;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = altura;
                    rotatedPreviewHeight = largura;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > LARGURA_MAXIMA) {
                    maxPreviewWidth = LARGURA_MAXIMA;
                }

                if (maxPreviewHeight > ALTURA_MAXIMA) {
                    maxPreviewHeight = ALTURA_MAXIMA;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                tamanhoDaExibição = escolheOTamanhoOtimo(mapa.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //superficie.setAspecoDaSuperficie(
                            //superficie.getWidth(), superficie.getHeight());
                } else {
                    //superficie.setAspecoDaSuperficie(
                            //superficie.getHeight(), superficie.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                suportaFlash = available == null ? false : available;

                idCamera = idDaCamera;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            DialogoDeErro.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENTO_DE_DIALOGO);
        }

    }

    private void iniciaThreadParaPlanoDeFundo() {

        threadParaPlanoDefundo = new HandlerThread("CameraBackground");
        threadParaPlanoDefundo.start();
        handlerParaPlanoDeFundo = new Handler(threadParaPlanoDefundo.getLooper());

    }

    private void pareAThreadDePlanoDeFundo(){

        threadParaPlanoDefundo.quitSafely();
        try {
            threadParaPlanoDefundo.join();
            threadParaPlanoDefundo = null;
            handlerParaPlanoDeFundo = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void capturaImagem() {
    }

    private void executaPrecaptura(){

    }


    private void abreCamera(int largura, int altura){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            solicitaPermissaoParaCamera();
            return;
        }
        configuraAsSaidasDaCamera(largura, altura);
        //modificaSuperficie(largura, altura);
        Activity activity = getActivity();
        CameraManager gerenciador = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!abreEFechaACamera.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Tempo de espera de foco da câmera excedido");
            }
            gerenciador.openCamera(idCamera, callbackParaOsEstadosDaCamera, handlerParaPlanoDeFundo);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrompido enquanto estava tentando tavar a câmera", e);
        }

    }

    private void fechaACamera(){

        try {
            abreEFechaACamera.acquire();
            if (null != sessaoDeCaptura) {
                sessaoDeCaptura.close();
                sessaoDeCaptura = null;
            }
            if (null != camera) {
                camera.close();
                camera = null;
            }
            if (null != leitorDeImagens) {
                leitorDeImagens.close();
                leitorDeImagens = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            abreEFechaACamera.release();
        }

    }

    private void criaSessaoDePreVisualizacao() {

        try {
            SurfaceTexture textura = textureView.getSurfaceTexture();

            assert textura != null;


            // We configure the size of default buffer to be the size of camera preview we want.
            textura.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(textura);

            leitorDeImagens = ImageReader.newInstance(textureView.getWidth(), textureView.getHeight(), ImageFormat.YUV_420_888, 1);

            leitorDeImagens.setOnImageAvailableListener(imagemDisponível, handlerParaPlanoDeFundo);

            // We set up a CaptureRequest.Builder with the output Surface.
            constroiRequisicaoDePrevisualizacao
                    = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            constroiRequisicaoDePrevisualizacao.addTarget(surface);
            constroiRequisicaoDePrevisualizacao.addTarget(leitorDeImagens.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            camera.createCaptureSession(Arrays.asList(surface,
                    leitorDeImagens.getSurface(), leitorDeImagensContinua.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == camera) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            sessaoDeCaptura = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                constroiRequisicaoDePrevisualizacao.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                configuraFlashAutomatico(constroiRequisicaoDePrevisualizacao);

                                // Finally, we start displaying the camera preview.
                                requisicaoDePrevisualizacao = constroiRequisicaoDePrevisualizacao.build();
                                sessaoDeCaptura.setRepeatingRequest(requisicaoDePrevisualizacao,
                                        callbackParaCapturas, handlerParaPlanoDeFundo);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void modificaSuperficie(int largura, int altura){

    }

    private void configuraFlashAutomatico(CaptureRequest.Builder builder){

        if (suportaFlash) {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    private byte[] convertYUV420888ToNV21(Image imgYUV420) {

        // Converting YUV_420_888 data to NV21.

        byte[] data;

        ByteBuffer buffer0 = imgYUV420.getPlanes()[0].getBuffer();

        ByteBuffer buffer2 = imgYUV420.getPlanes()[2].getBuffer();

        int buffer0_size = buffer0.remaining();

        int buffer2_size = buffer2.remaining();

        data = new byte[buffer0_size + buffer2_size];

        buffer0.get(data, 0, buffer0_size);

        buffer2.get(data, buffer0_size, buffer2_size);

        return data;

    }

    private class FrameProcessingRunnable implements Runnable {

        private Detector<Barcode> mDetector;

        private long mStartTimeMillis = SystemClock.elapsedRealtime();



        // This lock guards all of the member variables below.

        private final Object mLock = new Object();

        private boolean mActive = true;



        // These pending variables hold the state associated with the new frame awaiting processing.

        private long mPendingTimeMillis = SystemClock.elapsedRealtime();;

        private int mPendingFrameId = 0;

        private byte[] mPendingFrameData;
        private int imgLargura;
        private int imgAltura;



        FrameProcessingRunnable(Detector<Barcode> detector) {

            mDetector = detector;

        }



        /**

         * Releases the underlying receiver.  This is only safe to do after the associated thread

         * has completed, which is managed in camera source's release method above.

         */

        @SuppressLint("Assert")

        void release() {

            assert (mProcessingThread.getState() == Thread.State.TERMINATED);

            mDetector.release();

            mDetector = null;

        }



        /**

         * Marks the runnable as active/not active.  Signals any blocked threads to continue.

         */

        void setActive(boolean active) {

            synchronized (mLock) {

                mActive = active;

                mLock.notifyAll();

            }

        }



        /**

         * Sets the frame data received from the camera.

         */

        void setNextFrame(byte[] data, long timeMilis, int frameId, int largura, int altura) {

            synchronized (mLock) {

                if (mPendingFrameData != null) {

                    mPendingFrameData = null;

                }



                // Timestamp and frame ID are maintained here, which will give downstream code some

                // idea of the timing of frames received and when frames were dropped along the way.

                mPendingTimeMillis = SystemClock.elapsedRealtime() - mStartTimeMillis;;

                mPendingFrameId++;

                mPendingFrameData = data;

                imgLargura = largura;

                imgAltura = altura;



                // Notify the processor thread if it is waiting on the next frame (see below).

                mLock.notifyAll();

            }

        }



        /**

         * As long as the processing thread is active, this executes detection on frames

         * continuously.  The next pending frame is either immediately available or hasn't been

         * received yet.  Once it is available, we transfer the frame info to local variables and

         * run detection on that frame.  It immediately loops back for the next frame without

         * pausing.

         * <p/>

         * If detection takes longer than the time in between new frames from the camera, this will

         * mean that this loop will run without ever waiting on a frame, avoiding any context

         * switching or frame acquisition time latency.

         * <p/>

         * If you find that this is using more CPU than you'd like, you should probably decrease the

         * FPS setting above to allow for some idle time in between frames.

         */

        @Override

        public void run() {

            Frame outputFrame;



            while (true) {

                synchronized (mLock) {

                    while (mActive && (mPendingFrameData == null)) {

                        try {

                            // Wait for the next frame to be received from the camera, since we

                            // don't have it yet.

                            mLock.wait();

                        } catch (InterruptedException e) {

                            Log.d(TAG, "Frame processing loop terminated.", e);

                            return;

                        }

                    }



                    if (!mActive) {

                        // Exit the loop once this camera source is stopped or released.  We check

                        // this here, immediately after the wait() above, to handle the case where

                        // setActive(false) had been called, triggering the termination of this

                        // loop.

                        return;

                    }



                    outputFrame = new Frame.Builder()

                            .setImageData(ByteBuffer.wrap(mPendingFrameData), imgLargura, imgAltura, ImageFormat.NV21)

                            .setId(mPendingFrameId)

                            .setTimestampMillis(mPendingTimeMillis)

                            .setRotation(Frame.ROTATION_0)

                            .build();


                    //Toast.makeText(getParentFragment().getContext(), "Frame Criado Com Sucesso!", Toast.LENGTH_SHORT).show();

                    // We need to clear mPendingFrameData to ensure that this buffer isn't

                    // recycled back to the camera before we are done using that data.

                    mPendingFrameData = null;

                }





                // The code below needs to run outside of synchronization, because this will allow

                // the camera to add pending frame(s) while we are running detection on the current

                // frame.



                try {


                        mDetector.receiveFrame(outputFrame);


                } catch (Throwable t) {

                    Log.e(TAG, "Exception thrown from receiver.", t);

                }

            }

        }

    }

    private int getDetectorOrientation(int sensorOrientation) {
        switch (sensorOrientation) {
            case 0:
                return Frame.ROTATION_0;
            case 90:
                return Frame.ROTATION_90;
            case 180:
                return Frame.ROTATION_180;
            case 270:
                return Frame.ROTATION_270;
            case 360:
                return Frame.ROTATION_0;
            default:
                return Frame.ROTATION_90;
        }
    }
    private byte[] quarterNV21(byte[] data, int iWidth, int iHeight) {

        // Reduce to quarter size the NV21 frame

        byte[] yuv = new byte[iWidth/4 * iHeight/4 * 3 / 2];

        // halve yuma

        int i = 0;

        for (int y = 0; y < iHeight; y+=4) {

            for (int x = 0; x < iWidth; x+=4) {

                yuv[i] = data[y * iWidth + x];

                i++;

            }

        }

        return yuv;

    }

    /*
    * Esta classe implementa um comparator para comparar objetos Sizes e retornar o maior.
    */

    static class CompareTamanhosPorArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    //Cria um dialogo para interagir com o usuário e solicitar a permissão para acesso a câmera.

    public static class DialogoDeConfirmacao extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUISICAO_PERMISSAO_DA_CAMERA);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    //Casso a permissão não seja concedida uma mensagem de erro é exibida.
    public static class DialogoDeErro extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static DialogoDeErro newInstance(String message) {
            DialogoDeErro dialog = new DialogoDeErro();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //abreCamera(superficie.getmSurfaceView().getWidth(), superficie.getmSurfaceView().getHeight());

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    public void verificaGoogleAPi(){

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            abreCamera(textureView.getWidth(), textureView.getHeight());
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {}
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {

            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {}
    };

}
