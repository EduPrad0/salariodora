package br.com.salariodora;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private CheckBox cbValeTrans, cbValeAliment, cbValeRef;
    private EditText etNome, etSalBruto, etNumDep;
    private Button btCalcular, btLimpar;
    private TextView tvResult;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_saude);
        // buttons
        btCalcular = findViewById(R.id.bt_calc);
        btLimpar = findViewById(R.id.bt_limpar);


        // checkbox
        cbValeTrans = findViewById(R.id.vale_trans);
        cbValeAliment = findViewById(R.id.vale_aliment);
        cbValeRef = findViewById(R.id.vale_ref);

        // editsTexts
        etNome = findViewById(R.id.edit_name);
        etSalBruto = findViewById(R.id.edit_salBruto);
        etNumDep = findViewById(R.id.edit_numDep);

        // views
        tvResult = findViewById(R.id.tv_result);

        // valor spinner.getSelectedItemPosition(); pega a posição no array
        //  Posição 0 = default value -> deve retornar
        //  Posição 1 = Standard
        //  Posição 2 = Básico
        //  Posição 3 = Super
        //  Posição 4 = Master



        btCalcular.setOnClickListener(v -> {
            // Validação
           if(etNome.getText().toString().isEmpty()){
               Toast.makeText(getBaseContext(), R.string.valida_nome, Toast.LENGTH_LONG).show();
               return;
           }

           if(etSalBruto.getText().toString().isEmpty()) {
               Toast.makeText(getBaseContext(), R.string.valida_salBruto, Toast.LENGTH_LONG).show();
               return;
           }

            if(spinner.getSelectedItemPosition() == 0){
                Toast.makeText(getBaseContext(), R.string.valida_spinner, Toast.LENGTH_LONG).show();
                return;
            }

            // Validação |

           double descontos = 0, planoSaude = 0, calcInss = 0, valeTransporte = 0, valeAlimentacao = 0, valeRefeicao = 0, calcIRRF = 0;

           double slBt = Double.parseDouble(etSalBruto.getText().toString());

           // plano de saúde
           switch (spinner.getSelectedItemPosition()){
               case 1:
                   planoSaude = slBt < 3000 ? 60 : 80;
                   break;
               case 2:
                   planoSaude = slBt < 3000 ? 80 : 110;
                   break;
               case 3:
                   planoSaude = slBt < 3000 ? 95 : 135;
                   break;
               default:
                   planoSaude = slBt < 3000 ? 130 : 180;
                   break;
           }
            descontos += planoSaude;
            // plano de saúde |

           // Calcula inss
            if(slBt <= 1212.00) {
                calcInss = slBt * 0.075;
            } else if (slBt <= 2427.35) {
                calcInss = (slBt * 0.09) - 18.18;
            } else if(slBt <= 3641.03) {
                calcInss = (slBt * 0.12) - 91.00;
            } else if(slBt <= 7087.22) {
                calcInss = (slBt * 0.14) - 163.82;
            } else {
                 calcInss =  828.39;
            }

            descontos += calcInss;
            // Calcula inss |

            // verifica vales

            if(cbValeTrans.isChecked()){
                valeTransporte = slBt * 0.06;
                descontos += valeTransporte;
            }


            if(cbValeAliment.isChecked()){
                valeAlimentacao = slBt <= 3000 ? 15 : slBt <= 5000 ? 25 : 35;
                descontos += valeAlimentacao;
            }


            if(cbValeRef.isChecked()){
                valeRefeicao = slBt <= 3000 ? (2.6 * 22) : slBt <= 5000 ? (3.65 * 22) : (6.5 * 22);
                descontos += valeRefeicao;
            }

            // verifica vales |

            // verifica dependentes
            double deps = etNumDep.getText().toString().isEmpty() ||  Double.parseDouble(etNumDep.getText().toString()) <= 0
                    ? 0
                    : Double.parseDouble(etNumDep.getText().toString());

            // verifica dependentes |

            double base = slBt - calcInss - (189.59 * deps);

            if(base <= 1903.98){
                calcIRRF = 0;
            } else if(base <= 2826.65) {
                calcIRRF = (base * 0.075) - 142.80;
            } else if(base <= 3751.05) {
                calcIRRF = (base * 0.15) - 354.80;
            } else if(base <= 4664.68) {
                calcIRRF = (base * 0.225) - 636.13;
            } else {
                calcIRRF = (base * 0.275) - 869.36;
            }

            descontos += calcIRRF;

            tvResult.setVisibility(View.VISIBLE);

            String pattern = "00.##";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            tvResult.setText(etNome.getText() + "\n" + getText(R.string.salario_liquido) + " ->  R$ " + decimalFormat.format((slBt - descontos)) + "\n" +
                    getText(R.string.salario_bruto) + " -> R$ " + slBt + "\n" +
                    getText(R.string.inss)+ " -> R$ " + decimalFormat.format(calcInss) + "\n" +
                    (valeTransporte <= 0? "" : getText(R.string.vale_trans) + " -> R$ " + decimalFormat.format(valeTransporte) + "\n") +
                    (valeRefeicao <= 0? "" : getText(R.string.vale_ref)+ " -> R$ " + decimalFormat.format(valeRefeicao) + "\n") +
                    (valeAlimentacao <= 0? "" : getText(R.string.vale_aliment) + " -> R$ " + decimalFormat.format(valeAlimentacao) + "\n") +
                    getText(R.string.irrf) + " -> R$" + decimalFormat.format(calcIRRF) + "\n" +
                    getText(R.string.plano_saude) + " -> R$ " + decimalFormat.format(planoSaude)
            );
            Log.w("RESULTADO", "result : " + (slBt - descontos));

        });

        btLimpar.setOnClickListener(v -> {
            etNome.setText("");
            etSalBruto.setText("");
            etNumDep.setText("");
            spinner.setSelection(0);
            cbValeRef.setChecked(false);
            cbValeAliment.setChecked(false);
            cbValeTrans.setChecked(false);
            tvResult.setVisibility(View.GONE);
        });

    }

}