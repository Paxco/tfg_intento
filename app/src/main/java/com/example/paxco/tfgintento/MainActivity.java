package com.example.paxco.tfgintento;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paxco.tfgintento.R;
import com.mcc.ul.AiChanMode;
import com.mcc.ul.AiDevice;
import com.mcc.ul.AiScanOption;
import com.mcc.ul.AiUnit;
import com.mcc.ul.DaqDevice;
import com.mcc.ul.DaqDeviceDescriptor;
import com.mcc.ul.DaqDeviceManager;
import com.mcc.ul.Status;
import com.mcc.ul.ULException;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.EnumSet;


public class MainActivity extends AppCompatActivity {


    private double[][] curvas;
    private int tamcurva ;
    private int amplitud;
    TextView cuentatexto;
    public SeekBar barra;
    // DE LA LIBRERIA : GRAFICAS
    private GraphicalView gview;
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private XYSeries serieA, serieB;
    private XYSeriesRenderer serieAr, serieBr;

    // VARIABLES PARA LA TARJEDA DAQ

    private DaqDeviceManager ddm;
    private DaqDevice dd;
    private AiDevice aid;
    ArrayList<DaqDeviceDescriptor> ddi;

    // INTERFAZ DE USUARIO
    Button avance;
    Button paro;
    Button paro_emergencia;




    private void cambia(){
        cuentatexto.setText(String.valueOf(amplitud));
        serieA.clear();
        serieB.clear();
        for(int i=0;i<tamcurva;i++){
            curvas[0][i]=amplitud*Math.sin(2 * Math.PI * (double) i / tamcurva);
            serieA.add(i*360.0/tamcurva, curvas[0][i]);
            curvas[1][i]=amplitud*Math.cos(2 * Math.PI * (double) i / tamcurva);
            serieB.add(i*360.0/tamcurva, curvas[1][i]);
        }
        gview.repaint();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INICIALIZACION TARJETA DAQ

        ddm = new DaqDeviceManager(this);
        ddi = ddm.getDaqDeviceInventory();
        dd = ddm.createDaqDevice(ddi.get(0));

        // CONEXION A DAQ

        try{
            dd.connect();
            dd.flashLed(3);
            aid = dd.getAiDev();

        }
        catch (ULException e){
            e.printStackTrace();
        }


        tamcurva = 100;

        curvas = new double[2][tamcurva];
        for(int i=0;i<tamcurva;i++){
            curvas[0][i]=amplitud*Math.sin(2*Math.PI*(double)i/tamcurva);
            curvas[1][i]=amplitud*Math.cos(2 * Math.PI * (double) i / tamcurva);
        }


        // ADQUISICION DE DATOS

        try{
            aid.aInScan(0, 1, AiChanMode.DIFFERENTIAL, Range.BIP2PT5VOLTS,
                    100, 5000, EnumSet.of(AiScanOption.BURSTIO),
                    AiUnit.VOLTS, curvas);
            int aidstatus = aid.getStatus().currentStatus;
            while(aidstatus != Status.IDLE){
                SystemClock.sleep(10);
                aidstatus = aid.getStatus().currentStatus;
            }
            aid.stopBackground();
                }
        catch (ULException e){
            e.printStackTrace();
            }



        dataset = new XYMultipleSeriesDataset();
        renderer = new XYMultipleSeriesRenderer();
        serieA = new XYSeries("Seno");
        serieB = new XYSeries("Coseno");
        dataset.addSeries(serieA);
        dataset.addSeries(serieB);
        serieAr = new XYSeriesRenderer();
        serieBr = new XYSeriesRenderer();
        renderer.addSeriesRenderer(serieAr);
        renderer.addSeriesRenderer(serieBr);

        LinearLayout grafica = (LinearLayout) findViewById(R.id.grafica);
        gview = ChartFactory.getLineChartView(this, dataset, renderer);
        grafica.addView(gview, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        for(int i = 0; i < tamcurva; i++){
            serieA.add(i*360.0/tamcurva, curvas[0][i]);
            serieB.add(i*360.0/tamcurva, curvas[1][i]);
        }

// trazado de las graficas

        renderer.setMargins(new int[]{40, 60, 20, 20});
        renderer.setMarginsColor(Color.rgb(236, 236, 236));
        renderer.setAxesColor(Color.BLACK);
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        renderer.setLabelsTextSize(20);
        renderer.setXTitle("ángulo (º)");
        renderer.setYTitle("valor");
        renderer.setAxisTitleTextSize(20);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setYAxisMax(10);renderer.setYAxisMin(-10);
        renderer.setGridColor(Color.DKGRAY);
        renderer.setShowGrid(true);renderer.setLegendTextSize(20);
        serieAr.setColor(Color.BLUE);
        serieBr.setColor(Color.RED);




        //CONTROLES DEL USUARIO
                //BOTONES

        Button marcha=(Button) findViewById(R.id.marcha);
        //Button paro=(Button) findViewById(R.id.paro);
        //Button paro_emergencia=(Button) findViewById(R.id.paro_emergencia);

        marcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //pasamos la orden de marcha al VF


            }
        });


        /*paro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //pasamos la orden de paro al VF


            }
        });
        */

          /*paro_emergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //pasamos la orden de paro de emergencia al VF


            }
        });
        */



                //SEEKBAR
        SeekBar barra= (SeekBar)findViewById(R.id.barra);

        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                cambia();
                Toast.makeText(getApplicationContext(), String.valueOf(progress), Toast.LENGTH_LONG).show();

            }
        });




    }
}
