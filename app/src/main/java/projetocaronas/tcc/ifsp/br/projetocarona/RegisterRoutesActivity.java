package projetocaronas.tcc.ifsp.br.projetocarona;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import projetocaronas.tcc.ifsp.br.projetocarona.controllers.RouteController;
import projetocaronas.tcc.ifsp.br.projetocarona.utils.AndroidUtilsCaronas;

public class RegisterRoutesActivity extends FragmentActivity implements OnMapReadyCallback {
    // Elementos da busca
    private TextView textViewDeparture = null;
    private TextView textViewIntermediate = null;
    private TextView textViewDestination = null;

    private EditText editTextDeparture = null;
    private EditText editTextIntermediate = null;
    private EditText editTextDestination = null;

    private Button buttonAddIntermediatePlace = null;
    private Button buttonRemoceIntermediate = null;

    // Atributos para o Mapa
    private GoogleMap mMap;
    private LatLngBounds area = null;

    // Controlador de rotas
    private RouteController routeController = new RouteController();
    // Utilzado para prevenir múltiplos recarregamentos da rota devido a múltiplos cliques no mapa
    private boolean recalculateOnNextMapClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_routes);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtém elementos da busca
        textViewDeparture       = (TextView) findViewById(R.id.text_view_departure_place);
        textViewIntermediate    = (TextView) findViewById(R.id.text_view_intermediate_place);
        textViewDestination     = (TextView) findViewById(R.id.text_view_destination_place);

        editTextDeparture       = (EditText) findViewById(R.id.edit_text_departure_place);
        editTextIntermediate    = (EditText) findViewById(R.id.edit_text_intermediate_place);
        editTextDestination     = (EditText) findViewById(R.id.edit_text_destination_place);

        buttonAddIntermediatePlace = (Button) findViewById(R.id.button_add_intermediate_place);
        buttonRemoceIntermediate   = (Button) findViewById(R.id.button_remove_intermediate_place);

        // Botão que adiciona um ponto intermediário no trajeto
        buttonAddIntermediatePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esconde elemento
                editTextIntermediate.setVisibility(View.GONE);
                // Esconde Layout pai
                LinearLayout parent = (LinearLayout) editTextIntermediate.getParent();
                parent.setVisibility(View.VISIBLE);
            }
        });

        // Botão que remove o ponto intermediário
        buttonRemoceIntermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esconde elemento
                v.setVisibility(View.GONE);
                // Esconde Layout pai
                LinearLayout intermediateLayoutParent = (LinearLayout) v.getParent();
                intermediateLayoutParent.setVisibility(View.GONE);
            }
        });

        // Listener para os EditText, ao modificar, recalcular a rota
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                recalculateRoute(hasFocus);
            }
        };

        // Listeners para recalcular a rota
        editTextDeparture.setOnFocusChangeListener(onFocusChangeListener);
        editTextDestination.setOnFocusChangeListener(onFocusChangeListener);
        editTextIntermediate.setOnFocusChangeListener(onFocusChangeListener);


        // FIM Listeners para recalcular a rota

        // Listener para reativar o cálculo da rota caso algum EditText seja alterado
        TextWatcher textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recalculateOnNextMapClick = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        // Usando o TextChangedListener
        editTextIntermediate.addTextChangedListener(textChangedListener);
        editTextDeparture.addTextChangedListener(textChangedListener);
        editTextDestination.addTextChangedListener(textChangedListener);

    }

    private void recalculateRoute() {
        // Previne múltiplos recarregamentos de mapa após mais que um clique (sem alterar os EditText)
        recalculateOnNextMapClick = false;
        recalculateRoute(false);
    }

    private void recalculateRoute(boolean hasFocus) {
        String origin = editTextDeparture.getText().toString();
        String destination = editTextDestination.getText().toString();

        // Evento é disparado ao perder o foco
        if(! hasFocus) {
            if(! origin.isEmpty() && !destination.isEmpty()){
                List<String> textCoordinates = new ArrayList<String>();
                // Preenche a lista de String com os endereços informados nos editText

                textCoordinates.add(origin);

                textCoordinates.add(destination);
                // Adiciona apenas se estiver visível
                if(editTextIntermediate.getVisibility() == View.VISIBLE){
                    textCoordinates.add(editTextIntermediate.getText().toString());
                }

                // Chama o método passando a Activity e a lista de String
                List<LatLng> latLngs = routeController.parseToCoordinates(RegisterRoutesActivity.this, textCoordinates);
                routeController.getRouteJSON(getParent(), latLngs);
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Listener para recalcular a rota
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Esconde o teclado
                AndroidUtilsCaronas.hideKeyboard(RegisterRoutesActivity.this);
                // Recalcula a rota
                recalculateRoute();
            }
        });

        // Utiliza o Utils do Google Maps para decodificar o retorno da API //FIXME Testado, fazer receber da API
        // Morungaba a Bragança
        List<LatLng> decodePoints = PolyUtil.decode("rhxjCnzr|Gl@y@j@cBdAyD_C_EIGMWx@qGTqAd@cA|DiEpHLjB@t@M`O{IdEiHI}GFo@nA{BROPGy@UaAWwG}@yEo@_AJ_@LaCvB{Av@eBGcLsEcB{@Im@f@aBDSj@iFb@oBfAyCxAgK_@oC_CwDMaERgAd@_AzFkH`A}A`Aw@rA_BtDiIbCyHn@u@bCg@Vq@h@wEv@kBt@a@fAMV]?g@y@cAaBoAYuBAuAjAyA\\o@}AgDkDcDoEaGQsGXmBpBkC|AIPa@S{Be@wC[eK_@mGwDyJsAcIsB_HS]yA{@mCaBm@}AD_BxFkMVuA@cC|@kEl@kFHaCj@mB~BeM`FeILiAQw@}CaGy@}CwCsDgCcHe@gAo@g@cKoBgAk@sCkFeA{BwC_B{CeBuCgAgAu@eCoDqGaGmCkFoCsH}@uFgCkCq@cBsA_HD_Fb@s@`GeE~BmAt@w@`BgDhCqBp@sCrCoBjDo@nDOp@WjCuClDwAf@C|IlAdBIp@WvAk@|B[pSgA|EDt@]pEsErAqArAq@b@g@l@wBXeAv@iA`AkBrFqG~@_DrDaH`A}@fDy@~AaAx@gAvBNjAQzDyCb@iCp@}Cx@qBjF_J|BqDdCwAhBy@xDgDxDwBpDPlGvA~@EhEmAt@?zD|@xB`Az@J`Ai@~AcA~FmGfFeH~@u@~FoAjABfFxA\\E\\Y`BqCvH}GxD_CRYf@qCOcJRwBX{ApAqDXcBxCyPnDeTVoFn@_o@BqCOoCuCiOcAaFU}DFcCf@}DnFob@`G{f@L{BOaD_Gwc@o@}Dm@cBqA{B_A{AqCcFu@yBU_CDiCf@kCzNcd@bJ_Xp@eAj\\s^jLsM|AgEXyBv@q]v@k[IwBaAmDgBsBm@_@o_@mZkAeBo@sBQeEn@qDrJqSnAoArDiBtb@yQ~EmBdDWvE\\~BUjC}AlCmD~A{AlDiBpB_A|B}AlDgDtC{CxAaCv@yDVcGzA_GzGsRjCwILuEScBQs@kDiOS_GC{GVgABiEO}@OkJl@eEvCwJTaBAkBmAyEaBeEwH{S}CiGo@aBg@qEz@oNSwGc@cCq@aC\\mAPY`CeAvA[dCqB`@Wh@[D_@`AkFf@uBtBqIJMC_@[KMDyAaAm@{@?_ApEkJVc@Fk@Oc@}BcBsA_BgByCm@mDJmENyC]}@m@YgADoBB{BPmBGyCwAe@Ko@HcGjDU@uB{@wD{CqDyCgCmBu@UwMcBmFEeB@o@RcBr@mGTq@Dy@WgAe@oDVyGYy@GqA{@aCiIsAyEkBwIyJdJ");

//        List<LatLng> decodePoints = PolyUtil.decode("lkxjCpur|GdAyD_C_EkJGs\\Ti@a@Ek@o@wBiB}@yCeBk@JyANkCJ_CQs@YcTa@yWq@}@LiBx@e@l@wCtFqEt@kRhByETiM{Bih@aKm_@wHgNaDsBsAoBM{E?oAg@iBqAgAyC}BkEJcCUiCsBgCw@M}@P}Bp@sLXgDFi@Ea@]Ao@`A}AZiLIwK`@cApH[`HD^@PcB`@{B?}DSwEu@{COy@JuC|@}S\\cFXyABcB^mAhCgE~@sBn@mMj@eD|@cApFwDhBaEvAuEhCcFp@wBBoDi@{Ba@gDP{ChCcJ`F{Tb@sIb@cIlAyDlRyQfAkBFcBmDuIgCkG_F}[_@{Bq@aAmKyLyBuEcAaKr@sBvGaGpBiEXsBe@{]Wq@kN}IuE}GeB{D}BiDa@gAqAbEeDpC_DtAeM`DuEvAyDdBqOmBiF]{KcCoBu@eTkWeJaRgDgJq@eBeAkA}PkIa`A_Zqt@k]}BwAuAgCiBoGyBiIgAoD_UxP}C~BkCpBqM`KqChFy@bFCbB\\~Qd@fUe@gU]_RBcBx@cFz@yB`DwDpOkLl[aVdDuE~AsEtAoCrBkBbJkEzC{BrA}AxAwC|AsFvGuVlAsCfBcCxBkBpP_H`HeApKRvEb@fAIvAu@dDmCtAgBVyAAgDV{CrA_EtOw]xDmI`@eC{A{LLmOT{P]aScAyOe@sHCuAfAoCnBaCfG_AlFu@rHgE~FkBjG{@nBo@l@w@f@sBfAwGv@_Bp@[rA@rDbBfBKlAiAR{An@gAhD{CnGiPhKiBlEIlHdA`AEvEaH~B_CbE{ChJ}BzAcAhAwArAyEdB}Hn@mAfEsCzJkRhAaAdA[tNoDzF}AnBgBbCgEhAiI^iC`CcJpCmJpCcMdE_LfDiG`AoA`BaA|HeCfDg@jJYz[}A~Co@fPmHlN}GfI{FtAmBhHiVtFqRfCkE|E_GfEkPjEiN|DqOT}DdA_D~BeDfCkEfEeL`JyLxAeApKyDzRsKxJaGr`@yVxDgBvAyAt@yBY}Cc@eC@aAf@qA|@[vAJjGr@vDa@`CgA`CkCzAmDnBaMlEyPdBiAbKeCfNc@xBy@jKwCpAWfAq@zAOfU_@dLOxSOpQ|ChBe@xA{@|@aAlDmIt@wB`CgEjLkVhC}Fn@eDVUQ_As@JaDSoEs@KGM?iB{@aA}@eAgB?GAGIKoCoFBGd@q@g@u@i@o@cAyCO{AK{DJMfMaAlHqAl@EVMBYm@MG\\e@g@_AeLgDsX?QIK}@aHGU{@}FwAqJq@cCuDqMQY_BkIuGbGoClC");

        // Americana a Bragança
//        List<LatLng> decodePoints = PolyUtil.decode("xzvjC~b|}Gj@c@vA_ABAXWDGPO^]VW`@g@RWR[Ra@Xi@Rk@Ts@Ps@N_APsATwBLoALoAL}@VcBF[pAoG`BmInAsGRuANaAT}BL}ALmBFgA?MBu@NoHBUNcFHoBN{FNeFDuA@KFgAHuAH_AXgC\\cC\\iB^gBR{@h@kBv@gC`@kAl@yAbA}Bz@eBj@kALW|DeHFKxBgEp@qAz@aB~CcGjEoIv@{AP_@f@aALWnD}GlBuDlCiFlCgFlB{Dt@wA^s@hBqDZq@Xs@Vu@Po@No@Lq@Jq@DSBKBUDUBUBUBU@K@UBU@U@K@U@U?U@W?U?K?I?K?U?U?KAUAU?UCU?KCU?KCYCWCWKaAE_@UaBGe@Gc@G_@ScBk@cEo@eGGc@Ge@k@iE]_CQsAGs@Am@Ac@@W?e@@G@]De@Fe@Fe@Jg@Le@Nc@Ne@Ra@Ra@T_@V_@X]Z[b@a@RQXS^UfD_Bz@i@r@g@n@k@j@o@j@s@r@gAb@y@z@wA~@{AXg@TY\\c@V]VYZ]pAkAxBoBnDyCbB{A^]ZWXUROXO\\Sr@]fAa@l@Sv@Sp@Mh@Il@Gl@EnBKvBIjDOvAEVAdAAz@IXE\\EbAMfAUbAYhAe@^Mf@Sh@Ub@W`@W`@]b@a@d@g@f@m@f@w@Vi@Xs@\\{@f@_Br@oBfAsCTi@h@sAtAgDx@}Bv@oBRo@Nc@ZiAb@iBLi@RmANeAJ}@LgBD}@By@@]?e@CcAA][oEIq@Cm@Am@]_F{@_N?CC]Ce@]qGAQCa@AQCa@AQAOEa@Ec@AMC[E[C[CME[E]EYG[CMG[E[IYCMG[IYAIEQEKI[]eAy@kCy@iC]eAsBoF_CeHEKIWKUIWIUIWCKCKIWGWGWGYCKCKGWCKEYCKEYAKEWEYAKCYCKCYAKCYAYAKAYAWAY?WAY?K?K?Y?Y@W?K?M@W@Y?K@YBW@W@MBWBWBYBWDW@KDYDWpAaGPu@~AoIfAkFDS@CDUl@kCl@oCDUBSDSBI@IBIDQDSDQFQBGDQBIBGFQDIFOBIHOHOBGHQDGBGHOJOJOBGDEJODGJMDGLMDEJMLMLMDEFELK\\WNIZWNKZW\\WPMPOHEPOPMFGPOPONONOPQNONQLQNQNQDGNSLQLSLQJSJSJSHUJSHSHUDIFQHU@CBIDIBKHUBKFUFUBKFWFU@KBKFUDW@KDWDU@K@KDW@KBW@K@WBW?K@WBY?W@W?K@W?W?W?WAYAqAAg@CqAAqAAi@EqA?MKaDCaA_@eLAK?E?SAKGuBEcBA]?WASAY?m@?]?i@?Y@UBq@JqAB[BOFc@b@qBDUFUBIFUBIFSHUFSHUBIJSBIDIHSDIHSJQDIDIJSDGJSZ_@X_@LOZ]Z_@Z]Z_@LMZ_@TWtDcEtDeEtDgE~CoDJK@CFGjF_GnJyKnJoK|DgFbAqA\\s@zAaCjBcD`AiBnBqDhBkDt@uArAgC~GmMp@mAv@yAR_@R_@JKJOPYnAwAZ_@t@y@NWPWR]Xg@Tc@Zq@HQRe@DMN]Tk@DMb@kAPk@Pc@DMd@mAP_@HOh@eATe@Zm@b@w@x@}A^q@bAkBHO@CHQh@eAxAuC`@o@~@cBJSpBsDvCoF`GaLHO@AHQZk@fAmBbAaBjAeBf@s@Z_@tAeBnAwAn@o@l@o@ZW\\[b@[NQh@i@fBuA~BiBzAkAVS@AVS`GwEz@q@pAaATQxDuCfAw@zCaChHwFRO@ATQzC}BXWXWVWVWVWRS\\a@X_@V_@Xa@X_@Va@V_@Ta@^s@nBaExGuNdA}BlAmCBI`CmF~@qBBGBGDIDG`@aAn@wAP]R]LYLYpAoCjB}DzAcD~AmDFO~BgFpDyHxCiG|AoD\\w@pDmIn@}AXw@Vy@Pk@Ty@TaAP_ANcANiANcBF_AB}@DmAFaEB}@DcAD_@@UBWB[D[D[DYD[RkAlBoKdA{F~BmMx@cEb@{BTmATeAd@eBLe@Ng@Ng@HU\\cA\\aAJUN]Xu@f@iA`@{@d@_ArAiCHQpAgCjBsDx@_B\\q@HMTc@dAuBvDcHtAoCdAsBxA{ChBqDnAgC|A_DbDiGvDoHhHiNhDaHpAmC`BiDb@}@t@iBb@kA^kA^gA\\oAZgA\\{ANo@PeALu@VcBXeBlBmLp@uEh@kDZoBJi@Rw@Ni@HWL]j@mA\\k@h@y@`@i@bAcAj@e@ROXQj@[h@Sf@URG`DkAl@QlFmBPG|G_CBAv@]t@_@t@a@r@c@p@e@p@e@h@c@DEn@i@l@m@`@c@j@{@l@y@pAkBfBgCpByCzBuCz@qA~CsEtB}C^i@xBgDnBmCRWNSNSjBoBd@g@j@g@z@m@jA{@bB_AhAk@|Am@dBo@fA_@t@YzAg@`A_@hC_AxAk@j@Yr@]t@a@fAw@r@m@TSPOPQTUb@g@d@m@`@g@`@o@`@s@Zm@\\u@Xs@Vq@DSNk@J]XqANu@Lq@J_APwALsARmBl@sGl@cHNeBj@yFbAeLPeBLgALaAP_AReAd@sBZkAPq@BIHWPg@Zy@d@cAPg@`@cAjAaCl@_BLWVg@@E@APa@Zo@n@wAZs@P]b@aAjB_E|@uBv@_C`@uATaAb@qBN}@BQX_DDeAFiA@w@B{BB_EB}CB}FByCB_E@qBFiJDqHDsE@sABqA@u@BcA@u@Du@?SB]HsA`@_GRmCJ}A~AgTPiCHgABS?EH{ADa@PcCJuCH{BBmA?I@aC@cE@wE?}@?cG?yA@}A?gA@yBBwC?kD@{C?_CA{C?{B@cC@aA@yAB{@FgBFqAN}BJmAH}@ZkCVkBXgBBOPeAPy@H]Ja@H[l@}BxB_HfAyCrBoE~@iBbCyDjE}G`AuAdA{A@AtAmBhA_B`@o@b@o@p@aA~@qA|BcDZc@Zc@HK@CDG`CmD`AsAf@u@RYHKdBaCnBuCpBqC~A}BxAuBfA}ApAiB`ByBx@gA~AwBvAuB|@sAnAkBzA_ClAkBh@w@pAiBn@{@~AwBr@cAvDyFnDmFj@{@f@q@t@cAlJuMdD{EhBqCvDoGDG|AaDxHcOz@cBzA_DBC^w@n@kAp@eA\\m@Xa@d@q@h@q@vA{ATW^[\\]^[^[`@[dAs@hAs@dAq@zGiDfB{@rHkEhHmDZOvC{AlE{B~BmAtF{CfGaDxJ_FlH{DrEaCnBeAzEcCxG}DdAk@tBeAnBeArCwArAq@`Ak@fJeFhDiB`DkBd@WjAs@DEh@_@l@c@b@a@l@g@zAkBbAoA\\c@JORWPYHODIPY@AXg@Tg@L[Tg@FKJUNc@Pc@Na@Le@Nc@FO\\mAbA{DfA{GPoABI@EDYZwBp@{DF[@E?CH_@rCoO^sBXmBr@mE^oBV{A`@gCR_B@Kb@gDv@kFNgAf@{DH{@TwBZiDh@_H^}DVsET_JPiFFcDDqFAsF?iI?]?IA[AqB?yBBcDE}EIwEGgG@}B?{BKoHQkNEiEK{FGoCAoE?wA?sAEoHCiDM}FYmIEcAGaBKoB_@aHMwBAi@Ci@AQAWASAWEcAEeA");

        // Mercado Dia (+ perto)
//        List<LatLng> decodePoints = PolyUtil.decode("bdckClya{GwAzA}AdAmAb@{@T]FwCVoBNGAGBE@?@sGrBs@Fs@Ai@GUIKMOGO?QFCDED{@EoEc@iAQ[QAAECM?C?u@Yq@a@g@a@eAoAYs@?A?C?GKMEAC?kCgFBABG@Cd@q@g@u@i@o@{@kBAKEa@Gk@O}AMuADs@DEDEBEvDQnGo@zAShBa@fB[b@GD@LAJGDM?MCKKGMAMBGJCN@BMGQQEMIgAAo@OyBSuB_@{BqAgJk@mEUgCEu@BGAIGMCAm@oDQgB@A?A@A@GAEEICASoAiBaN]{A{CiKaAkDEMKK_BkIuGbGcB`B");
        // Bauru (+ Longe)
//        List<LatLng> decodePoints = PolyUtil.decode("jjfgCrghjH_BwTs^|GyIyXoKis@uNa_AoCaj@rp@au@tO~AmCjApAg^jSuqBprAkkMrv@g_IV}oAgMqeBe_@ubEc^{vDs\\w_Cit@enAo^uq@mLay@eRaiIkPgjHiGi_Eot@rE_w@pDeQme@{Xw]w_BmdAc[yUcEmVph@sqCn]ieFjJuuAqNwdBwPwwAZ{~CfOuaCqIkeAe~@s}Buc@m^mJgc@eKqOrBmNzJeu@VgTaXiz@{K}a@hA_k@fIihBtSy|@sB{yA}]slAvJw_@pY}a@`}@s}@xNg[fPibBhO{{APmW}L_l@}Qgo@uAo[`Sk`CdTcjCdMmnAtJqVjj@cw@fcAiwAvFiSAgt@UaxGg@sxAsAcbA}QmfAqGq[kVeiAyZqWwFu[aRooC`B_bA_e@alAuOclBuSyrCsh@_|GeNuu@}l@_u@`AwmAws@eeB{iAalCkl@cy@cFeUpImKh[{q@psBkbGx\\}u@v[iPthB`Ch_@sV|Tg[vFaRwB}\\fGaX|g@i]zc@iP``Agd@ft@_QtUi`@tUiOfd@wQjSZzr@c\\fMsCxy@Gnr@}e@|i@_gA~wBgcCl_BocBncC{lBnuAgk@nj@mVr_Amu@dv@}qA`Uy|@h@{i@~Jup@db@{qAjx@amDn~@wsElh@wkCtl@wkC`n@mTx|@cZzqCsaBjvAa{@`d@sJlcBpElfA`Bv`Boc@j\\id@lTmWlvBonBzp@yc@p_B}d@v}A}z@ds@_HjkAp@rb@eSf{@aq@jf@qUxpAiX|[{Sbp@mcBbwAw`BpzAgbBlYq^jq@wZnhCyhAlh@}f@fh@ky@bl@esAvp@imAzeBccBl|@u}AlGsHeFyIuc@}eDah@muEaV}rDxK}|AlUyl@p]ob@x}EkxArn@iZdLgOpSqwBtfAm}B|BcWoG_y@tl@uo@nWuJ|^mF`Tgc@lFy^uGu{@wQsl@|MyeAzWo\\~AyoA~KuRleAaqAp^qn@jZ}m@z`@ap@`^oYva@w\\|[wp@l^wv@`Pkl@dX}vAnmAygCrN{q@zb@wRx`AkfAn_@aQjKyUpLmeAxJaV|Ncj@`A_lAxGgaCtDkk@|`@gx@l`AytA|pA{oBdaDodBzW_n@vNa|@`I{`BuEc}CvD{C_GtEah@hTo{@mQic @}CgMaSer@~S{k@bKy]}Fq_B{nAsUo`@gSo{@eoBylBckBin @mb @kn @eUyNgO{EcN_`@qDaYkB_HcBrIwFhQaEdUmVhRu\\rVoUlFgHfNmAdHcKl@}EuBg^dCa@AUJoIbNgf @v_ @yPhP");


        // Desenha a polilinha de acordo com as coordenadas
        PolylineOptions options=new PolylineOptions();
        options.width(12);
        options.color(Color.CYAN);
        options.addAll(decodePoints);

        googleMap.addPolyline(options);// ou mMap


        // Cria os Marcadores inicial e final
        LatLng startLatLng = decodePoints.get(0);
        LatLng endLatLng = decodePoints.get(decodePoints.size() - 1);
        // Adiciona os marcadores ao mapa
        mMap.addMarker(new MarkerOptions().position(startLatLng).title("Início da rota"));
        mMap.addMarker(new MarkerOptions().position(endLatLng).title("Fim da rota"));


        area = getAreaToDraw(startLatLng, endLatLng);

        //FIXME Refator 2 métodos em um
        // Coloca a câmera no maior zoom possivel que inclua as coordenadas
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(area.getCenter(),10));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(area, 90));
                mMap.setOnCameraChangeListener(null);
            }
        });

//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(area, 10));


    }

    /** DEPRECATED: Utilizar o retorno da API Maps que já devolve sudeste e noroeste
     * Faz a delimitação e retorna a área a ser exibida no mapa a partir de duas coordenadas passadas por parâmetro.
     * Já faz o tratamento para determinar os extremos sudoeste e nordeste, as coordenadas podem estar em qualquer ordem.
     * @param startLatLng Coordenada 1
     * @param endLatLng   Coordenada 2
     * @return LatLngBounds com a área a ser visualizada
     */
    @Deprecated
    private LatLngBounds getAreaToDraw(LatLng startLatLng, LatLng endLatLng) {
        // A latitude do 1º deve ser menor do que a do 2º parâmetro

        double minLatitude = startLatLng.latitude <= endLatLng.latitude ? startLatLng.latitude : endLatLng.latitude; //FIXME Check if EQUAL condition
        double minLongitude = startLatLng.longitude <= endLatLng.longitude ? startLatLng.longitude : endLatLng.longitude; //FIXME Check if EQUAL condition
        double maxLatitude = startLatLng.latitude > endLatLng.latitude ? startLatLng.latitude : endLatLng.latitude; //FIXME Check if EQUAL condition
        double maxLongitude = startLatLng.longitude > endLatLng.longitude ? startLatLng.longitude : endLatLng.longitude; //FIXME Check if EQUAL condition

        LatLng southwest = new LatLng(minLatitude,minLongitude);
        LatLng northeast = new LatLng(maxLatitude, maxLongitude);

        //northeast = Nordeste => Lat e Lon maiores
        //southwest = Sudoeste => Lat e Lon menores
        return new LatLngBounds(southwest, northeast);

    }

}
