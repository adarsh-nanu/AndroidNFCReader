package com.example.myfirstapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.myfirstapp.genericUtil.TDes;
import com.example.myfirstapp.genericUtil.ANSIPin;

/**
 * Created by adarsh on 04/03/17.
 */

public class PINPad extends DialogFragment {
    private String PIN = "";
    String Module = this.getClass().getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState )
    {
        final View view = layoutInflater.inflate(R.layout.pinpad, container, false);
        Button cancelButton = (Button) view.findViewById(R.id.pincancel );
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinBox = (EditText) view.findViewById( R.id.pinbox );
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                PINPad pinpadFragment = ( PINPad) fragmentManager.findFragmentByTag("Enter PIN");
                fragmentTransaction.remove( pinpadFragment );
                fragmentTransaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_CLOSE );
                fragmentTransaction.commit();
            }
        });

        Button okButton = (Button)view.findViewById( R.id.pinok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinBox = (EditText) view.findViewById( R.id.pinbox );
                //do asii pin calculaton and encrypted pin block
                //either return back or ask to re-enter PIN
                if( PIN.length() < 4 )
                    return;
                else {
                    if (getEncryptedPINBlock()) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        PINPad pinpadFragment = (PINPad) fragmentManager.findFragmentByTag("Enter PIN");
                        fragmentTransaction.remove(pinpadFragment);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                        fragmentTransaction.commit();
                    }
                }
            }
        });

        Button zeroButton = (Button)view.findViewById( R.id.pin0);
        zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(0));
            }
        });

        Button oneButton = (Button)view.findViewById( R.id.pin1);
        oneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(1));
            }
        });

        Button twoButton = (Button)view.findViewById( R.id.pin2);
        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(2));
            }
        });

        Button threeButton = (Button)view.findViewById( R.id.pin3);
        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(3));
            }
        });

        Button fourButton = (Button)view.findViewById( R.id.pin4);
        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(4));
            }
        });

        Button fiveButton = (Button)view.findViewById( R.id.pin5);
        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(5));
            }
        });

        Button sixButton = (Button)view.findViewById( R.id.pin6);
        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(6));
            }
        });

        Button sevenButton = (Button)view.findViewById( R.id.pin7);
        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(7));
            }
        });

        Button eightButton = (Button)view.findViewById( R.id.pin8 );
        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(8));
            }
        });

        Button nineButton = (Button)view.findViewById( R.id.pin9);
        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinbox = (EditText) view.findViewById(R.id.pinbox);
                pinbox.setText(updatePIN(9));
            }
        });
        return view;
    }

    public String updatePIN( int pinDigit )
    {
        return PIN = PIN.concat( String.valueOf( pinDigit ));
    }

    public boolean getEncryptedPINBlock()
    {
        ANSIPin ansiPin = new ANSIPin();
        TDes tDes = new TDes( "F94E86C832C94D16819F2FD11C240F83", ansiPin.format0( new String( new DataFormatterUtil().BCDToChar( new CardData().getIssuerPAN() ) ), PIN) );
        byte[] pinBlock = tDes.encrypt();
        if( pinBlock.length == 8 )
            return true;
        else
            return false;
    }

}
