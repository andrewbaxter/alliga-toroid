package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.display.Drawing;
import com.zarbosoft.merman.core.display.DrawingContext;
import com.zarbosoft.merman.core.syntax.style.ModelColor;
import com.zarbosoft.merman.core.visual.Vector;

public class Icons {
  public static Drawing gear(Context context, double scale, double thickness, ModelColor color) {
    scale *= context.toPixels;
    final Drawing out = context.display.drawing();
    out.resize(context, new Vector(scale, scale));
    final DrawingContext gc = out.begin(context);
    gc.setLineThickness(thickness * context.toPixels);
    gc.setLineColor(color);

    gc.beginStrokePath();
    gc.moveTo(0.432539875384012 * scale, 0.1218140143195019 * scale);
    gc.moveTo(0.418570933893332 * scale, 0.1773925262026335 * scale);
    gc.splineTo(
        0.415721371429232 * scale,
        0.1887299483514665 * scale,
        0.404278142406732 * scale,
        0.20049697445322048 * scale,
        0.393299257128382 * scale,
        0.2045052690277223 * scale);
    gc.splineTo(
        0.384601344737962 * scale,
        0.20768079815438228 * scale,
        0.376047894845722 * scale,
        0.2112389941512213 * scale,
        0.36766404286801196 * scale,
        0.2151694853517033 * scale);
    gc.splineTo(
        0.357081503418002 * scale,
        0.2201307667475323 * scale,
        0.34066400195817 * scale,
        0.2199599517496843 * scale,
        0.33060816854319097 * scale,
        0.2139987041164533 * scale);
    gc.lineTo(0.280723997921653 * scale, 0.1844266142598484 * scale);
    gc.splineTo(
        0.270017633473203 * scale,
        0.17807971790230928 * scale,
        0.2563680176035 * scale,
        0.1798170196720887 * scale,
        0.2475928786307196 * scale,
        0.18864347458172262 * scale);
    gc.lineTo(0.18907296740964502 * scale, 0.2475056084651524 * scale);
    gc.splineTo(
        0.1802968150827107 * scale,
        0.2563330820206068 * scale,
        0.178635446082808 * scale,
        0.269992709723517 * scale,
        0.18503973321045739 * scale,
        0.28066653042237705 * scale);
    gc.lineTo(0.21441908204865717 * scale, 0.3296321206388211 * scale);
    gc.splineTo(
        0.22043351093121819 * scale,
        0.3396561246792011 * scale,
        0.22060604572071119 * scale,
        0.3560245723896511 * scale,
        0.21559171536721716 * scale,
        0.3665820028816441 * scale);
    gc.splineTo(
        0.21152178875182517 * scale,
        0.37515106319035413 * scale,
        0.20784169921485515 * scale,
        0.3838997755801341 * scale,
        0.20456290321449616 * scale,
        0.3928014171346541 * scale);
    gc.splineTo(
        0.20052316555705715 * scale,
        0.4037689253298141 * scale,
        0.18874177258048416 * scale,
        0.4152507835184941 * scale,
        0.17741882843146875 * scale,
        0.4181572313985441 * scale);
    gc.lineTo(0.12178796079899674 * scale, 0.43243679371864413 * scale);
    gc.splineTo(
        0.10972229949267175 * scale,
        0.43553374159629415 * scale,
        0.10128592772395674 * scale,
        0.4464091747926141 * scale,
        0.10128592772395674 * scale,
        0.45886602255234415 * scale);
    gc.lineTo(0.10128592772395674 * scale, 0.54158964109349 * scale);
    gc.splineTo(
        0.10128592772395674 * scale,
        0.5540464888532199 * scale,
        0.10972229949267173 * scale,
        0.56492192204954 * scale,
        0.12178796079899674 * scale,
        0.56801886992719 * scale);
    gc.lineTo(0.17741882843146875 * scale, 0.5822984322472899 * scale);
    gc.splineTo(
        0.18874177787215074 * scale,
        0.58520488012734 * scale,
        0.20055621730664075 * scale,
        0.5966774778994699 * scale,
        0.20463558160941386 * scale,
        0.60763016942815 * scale);
    gc.splineTo(
        0.20789082969340286 * scale,
        0.61637015056804 * scale,
        0.21153337485584586 * scale,
        0.62496117129314 * scale,
        0.21555242209687886 * scale,
        0.63337730243711 * scale);
    gc.splineTo(
        0.22058892453342685 * scale,
        0.64392412313757 * scale,
        0.22045538932677586 * scale,
        0.660295587097982 * scale,
        0.21446763044387887 * scale,
        0.670335651346493 * scale);
    gc.lineTo(0.18499138060689987 * scale, 0.7197602407238209 * scale);
    gc.splineTo(
        0.17861182672893888 * scale,
        0.730457344755721 * scale,
        0.18029174566610787 * scale,
        0.744117517500291 * scale,
        0.18907297534714487 * scale,
        0.752950102805681 * scale);
    gc.lineTo(0.24759287863071958 * scale, 0.8118122472724439 * scale);
    gc.splineTo(
        0.2563729970617706 * scale,
        0.820643774244514 * scale,
        0.27002314209813355 * scale,
        0.822399284639064 * scale,
        0.280752176046298 * scale,
        0.816076801385384 * scale);
    gc.lineTo(0.331096774578702 * scale, 0.78640939009248 * scale);
    gc.splineTo(
        0.34116813903515203 * scale,
        0.78047452141725 * scale,
        0.357612363411314 * scale,
        0.78030016100278 * scale,
        0.36820977244447 * scale,
        0.78522934844068 * scale);
    gc.splineTo(
        0.37625469317645 * scale,
        0.7889713504768701 * scale,
        0.38445439515648 * scale,
        0.79237124626737 * scale,
        0.39278691796817 * scale,
        0.79541977539563 * scale);
    gc.splineTo(
        0.40376315741322 * scale,
        0.79943562117837 * scale,
        0.41523893018531 * scale,
        0.8112000543634901 * scale,
        0.41813082598221 * scale,
        0.8225267079707921 * scale);
    gc.lineTo(0.43246304038498 * scale, 0.878660892471923 * scale);
    gc.splineTo(
        0.4355438486795 * scale,
        0.8907272152365731 * scale,
        0.44641266729257 * scale,
        0.899169540130213 * scale,
        0.45886607546901004 * scale,
        0.899169540130213 * scale);
    gc.lineTo(0.542106451711979 * scale, 0.899169540130213 * scale);
    gc.splineTo(
        0.554559859888419 * scale,
        0.899169540130213 * scale,
        0.565428678501489 * scale,
        0.890727215236573 * scale,
        0.568509486796009 * scale,
        0.878660892471923 * scale);
    gc.lineTo(0.582841701198779 * scale, 0.8225267079707921 * scale);
    gc.splineTo(
        0.585733596995679 * scale,
        0.811199895613492 * scale,
        0.5971823822681089 * scale,
        0.7993415353462221 * scale,
        0.608116817547019 * scale,
        0.7952130828982341 * scale);
    gc.splineTo(
        0.6163308070268689 * scale,
        0.7921116371039741 * scale,
        0.624411446508399 * scale,
        0.7886675558973641 * scale,
        0.632337040158549 * scale,
        0.7848895705282941 * scale);
    gc.splineTo(
        0.6428875650256289 * scale,
        0.7798606351749842 * scale,
        0.6592795871107819 * scale,
        0.7799709664235941 * scale,
        0.669339706775707 * scale,
        0.7859240913485941 * scale);
    gc.lineTo(0.7202396921761139 * scale, 0.8160439136774651 * scale);
    gc.splineTo(
        0.730952935791144 * scale,
        0.8223833302642651 * scale,
        0.7446027897858439 * scale,
        0.8206402552862251 * scale,
        0.7533796009252699 * scale,
        0.8118121678974451 * scale);
    gc.lineTo(0.8118995068546779 * scale, 0.752950102805681 * scale);
    gc.splineTo(
        0.8206773234107579 * scale,
        0.744120957083581 * scale,
        0.8223449921397479 * scale,
        0.730461048922341 * scale,
        0.8159486901369979 * scale,
        0.7197795817652439 * scale);
    gc.lineTo(0.7860206378057111 * scale, 0.669799658436579 * scale);
    gc.splineTo(
        0.780014860798041 * scale,
        0.659770098146269 * scale,
        0.779825419133761 * scale,
        0.643383923352709 * scale,
        0.7848088461543111 * scale,
        0.632811755569235 * scale);
    gc.splineTo(
        0.7886886961054311 * scale,
        0.624580832756265 * scale,
        0.7922092418944111 * scale,
        0.616185603695365 * scale,
        0.7953609585213711 * scale,
        0.607649351719575 * scale);
    gc.splineTo(
        0.7994090834703711 * scale,
        0.596685018524375 * scale,
        0.8112034145717811 * scale,
        0.5852285603353751 * scale,
        0.8225336135957051 * scale,
        0.582350952038295 * scale);
    gc.lineTo(0.8791712737154931 * scale, 0.5679668793028451 * scale);
    gc.splineTo(
        0.891238125646803 * scale,
        0.5649022105914551 * scale,
        0.8996868005403631 * scale,
        0.5540400065616351 * scale,
        0.8996868005403631 * scale,
        0.541590037968485 * scale);
    gc.lineTo(0.8996868005403631 * scale, 0.458866049010677 * scale);
    gc.splineTo(
        0.8996868005403631 * scale,
        0.446416080417527 * scale,
        0.891238125646803 * scale,
        0.43555387638770704 * scale,
        0.8791712737154931 * scale,
        0.432489207676317 * scale);
    gc.lineTo(0.8225336135957051 * scale, 0.41810494973253604 * scale);
    gc.splineTo(
        0.811203335196782 * scale,
        0.415227420810455 * scale,
        0.799501608260872 * scale,
        0.40374495408011596 * scale,
        0.7955650316438 * scale,
        0.392739875052096 * scale);
    gc.splineTo(
        0.792468586474477 * scale,
        0.38408350224448595 * scale,
        0.7889938929765861 * scale,
        0.375567332143443 * scale,
        0.785151031775 * scale,
        0.367215521206996 * scale);
    gc.splineTo(
        0.780265553503216 * scale,
        0.356597765715763 * scale,
        0.780517807250038 * scale,
        0.340160314672849 * scale,
        0.7865056984246 * scale,
        0.33011982709101 * scale);
    gc.lineTo(0.815981101594923 * scale, 0.280695449380346 * scale);
    gc.splineTo(
        0.822360470264553 * scale,
        0.269998345348446 * scale,
        0.820680630702383 * scale,
        0.256338199062209 * scale,
        0.811899374563013 * scale,
        0.2475056084651524 * scale);
    gc.lineTo(0.75337960092527 * scale, 0.18864347458172262 * scale);
    gc.splineTo(
        0.7446044566608231 * scale,
        0.1798170170262554 * scale,
        0.73095484079112 * scale,
        0.17807973377730912 * scale,
        0.72024847634267 * scale,
        0.1844266248431816 * scale);
    gc.lineTo(0.6703643057211319 * scale, 0.21399868030395358 * scale);
    gc.splineTo(
        0.6603085516811519 * scale,
        0.21995990147885158 * scale,
        0.6438909179296549 * scale,
        0.2201307693933656 * scale,
        0.633308352021312 * scale,
        0.21516946153920358 * scale);
    gc.splineTo(
        0.624924500043602 * scale,
        0.21123897033872158 * scale,
        0.616371050151362 * scale,
        0.2076807743418826 * scale,
        0.607673137760942 * scale,
        0.20450524521522256 * scale);
    gc.splineTo(
        0.596694252482592 * scale,
        0.20049696651572058 * scale,
        0.5852510234600921 * scale,
        0.18872999333063256 * scale,
        0.582401460995992 * scale,
        0.17739253678596667 * scale);
    gc.lineTo(0.568432598880311 * scale, 0.1218140143195019 * scale);
    gc.splineTo(
        0.5653994155851909 * scale,
        0.1097461834298709 * scale,
        0.5545496469718809 * scale,
        0.10128581395312489 * scale,
        0.5421065575453109 * scale,
        0.10128581395312489 * scale);
    gc.lineTo(0.458866049010677 * scale, 0.10128581395312489 * scale);
    gc.splineTo(
        0.446422959584107 * scale,
        0.10128581395312489 * scale,
        0.43557292638746703 * scale,
        0.10974613051320489 * scale,
        0.432540007675677 * scale,
        0.1218140143195019 * scale);
    gc.closePath();
    gc.beginStrokePath();
    gc.moveTo(0.60057168 * scale, 0.49973753 * scale);
    gc.splineTo(
        0.60057168 * scale,
        0.55524523 * scale,
        0.55557378 * scale,
        0.60024311 * scale,
        0.5000661000000001 * scale,
        0.60024311 * scale);
    gc.splineTo(
        0.4445584000000001 * scale,
        0.60024311 * scale,
        0.3995605200000001 * scale,
        0.5552452099999999 * scale,
        0.3995605200000001 * scale,
        0.49973753 * scale);
    gc.splineTo(
        0.3995605200000001 * scale,
        0.44422983 * scale,
        0.44455842000000007 * scale,
        0.39923195 * scale,
        0.5000661000000001 * scale,
        0.39923195 * scale);
    gc.splineTo(
        0.5555738000000001 * scale,
        0.39923195 * scale,
        0.60057168 * scale,
        0.44422985 * scale,
        0.60057168 * scale,
        0.49973753 * scale);
    gc.closePath();

    return out;
  }

  public static Drawing error(Context context, double scale, double thickness, ModelColor color) {
    scale *= context.toPixels;
    final Drawing out = context.display.drawing();
    out.resize(context, new Vector(scale, scale));
    final DrawingContext gc = out.begin(context);
    gc.setLineThickness(thickness * context.toPixels);
    gc.setLineColor(color);

    gc.beginStrokePath();
    gc.moveTo(0.859797 * scale, 0.50016356 * scale);
    gc.splineTo(
        0.859797 * scale,
        0.6988456799999999 * scale,
        0.6987333200000001 * scale,
        0.85990936 * scale,
        0.5000512 * scale,
        0.85990936 * scale);
    gc.splineTo(
        0.30136908000000007 * scale,
        0.85990936 * scale,
        0.14030540000000002 * scale,
        0.69884568 * scale,
        0.14030540000000002 * scale,
        0.50016356 * scale);
    gc.splineTo(
        0.14030539000000003 * scale,
        0.30148144 * scale,
        0.30136908 * scale,
        0.14041774999999995 * scale,
        0.5000512 * scale,
        0.14041774999999995 * scale);
    gc.splineTo(
        0.69873332 * scale,
        0.14041774999999995 * scale,
        0.8597970100000001 * scale,
        0.30148144 * scale,
        0.859797 * scale,
        0.50016356 * scale);
    gc.closePath();
    gc.beginStrokePath();
    gc.moveTo(0.55393652 * scale, 0.70048565 * scale);
    gc.splineTo(
        0.55393654 * scale,
        0.73032055 * scale,
        0.52975052 * scale,
        0.75450655 * scale,
        0.49991562000000006 * scale,
        0.75450655 * scale);
    gc.splineTo(
        0.47008072000000006 * scale,
        0.75450657 * scale,
        0.4458947200000001 * scale,
        0.73032055 * scale,
        0.4458947200000001 * scale,
        0.70048565 * scale);
    gc.splineTo(
        0.4458947000000001 * scale,
        0.67065075 * scale,
        0.47008072000000006 * scale,
        0.6464647499999999 * scale,
        0.49991562000000006 * scale,
        0.6464647499999999 * scale);
    gc.splineTo(
        0.5297505200000001 * scale,
        0.6464647299999999 * scale,
        0.55393652 * scale,
        0.67065075 * scale,
        0.55393652 * scale,
        0.70048565 * scale);
    gc.closePath();
    gc.beginStrokePath();
    gc.moveTo(0.50017726 * scale, 0.24764189 * scale);
    gc.splineTo(
        0.53134156 * scale,
        0.24764189 * scale,
        0.55559856 * scale,
        0.27189889 * scale,
        0.55559856 * scale,
        0.30202969 * scale);
    gc.splineTo(
        0.55559856 * scale,
        0.37480509 * scale,
        0.55559856 * scale,
        0.44758046 * scale,
        0.55559856 * scale,
        0.5203558500000001 * scale);
    gc.splineTo(
        0.55559856 * scale,
        0.5504866500000001 * scale,
        0.53134156 * scale,
        0.57474365 * scale,
        0.50017726 * scale,
        0.57474365 * scale);
    gc.splineTo(
        0.46901296 * scale,
        0.57474365 * scale,
        0.44475595999999995 * scale,
        0.5504866500000001 * scale,
        0.44475595999999995 * scale,
        0.5203558500000001 * scale);
    gc.splineTo(
        0.44475595999999995 * scale,
        0.4475804500000001 * scale,
        0.44475595999999995 * scale,
        0.3748050700000001 * scale,
        0.44475595999999995 * scale,
        0.3020296800000001 * scale);
    gc.splineTo(
        0.44475595999999995 * scale,
        0.27189888000000006 * scale,
        0.4690129599999999 * scale,
        0.2476418800000001 * scale,
        0.50017726 * scale,
        0.2476418800000001 * scale);
    gc.closePath();

    return out;
  }

  public static Drawing floppy(Context context, double scale, double thickness, ModelColor color) {
    scale *= context.toPixels;
    final Drawing out = context.display.drawing();
    out.resize(context, new Vector(scale, scale));
    final DrawingContext gc = out.begin(context);
    gc.setLineThickness(thickness * context.toPixels);
    gc.setLineColor(color);

    gc.beginStrokePath();
    gc.moveTo(0.13554269 * scale, 0.09291539 * scale);
    gc.splineTo(
        0.37896447 * scale,
        0.09291539 * scale,
        0.62238625 * scale,
        0.09291539 * scale,
        0.86580803 * scale,
        0.09291539 * scale);
    gc.splineTo(
        0.88930683 * scale,
        0.09291539 * scale,
        0.90822463 * scale,
        0.1118332 * scale,
        0.90822463 * scale,
        0.13533201 * scale);
    gc.splineTo(
        0.90822463 * scale,
        0.37872371 * scale,
        0.90822463 * scale,
        0.6221154 * scale,
        0.90822463 * scale,
        0.8655071000000001 * scale);
    gc.splineTo(
        0.90822463 * scale,
        0.8890059000000001 * scale,
        0.88930683 * scale,
        0.9079237000000001 * scale,
        0.86580803 * scale,
        0.9079237000000001 * scale);
    gc.splineTo(
        0.83183783 * scale,
        0.9079237000000001 * scale,
        0.7978676299999999 * scale,
        0.9079237000000001 * scale,
        0.7638974599999999 * scale,
        0.9079237000000001 * scale);
    gc.splineTo(
        0.7638974599999999 * scale,
        0.8339538000000001 * scale,
        0.7638974599999999 * scale,
        0.7599838900000001 * scale,
        0.7638974599999999 * scale,
        0.6860139800000001 * scale);
    gc.splineTo(
        0.6275781999999999 * scale,
        0.6860139800000001 * scale,
        0.4912589499999999 * scale,
        0.6860139800000001 * scale,
        0.3549396899999999 * scale,
        0.6860139800000001 * scale);
    gc.splineTo(
        0.3549396899999999 * scale,
        0.7599838800000002 * scale,
        0.3549396899999999 * scale,
        0.8339537900000001 * scale,
        0.3549396899999999 * scale,
        0.9079237000000001 * scale);
    gc.splineTo(
        0.28180738999999994 * scale,
        0.9079237000000001 * scale,
        0.2086750199999999 * scale,
        0.9079237000000001 * scale,
        0.1355426899999999 * scale,
        0.9079237000000001 * scale);
    gc.splineTo(
        0.11204388999999991 * scale,
        0.9079237000000001 * scale,
        0.09312606999999991 * scale,
        0.8890059000000001 * scale,
        0.09312606999999991 * scale,
        0.8655071000000001 * scale);
    gc.splineTo(
        0.09312606999999991 * scale,
        0.6221154000000001 * scale,
        0.09312606999999991 * scale,
        0.3787237100000001 * scale,
        0.09312606999999991 * scale,
        0.13533201000000006 * scale);
    gc.splineTo(
        0.09312606999999991 * scale,
        0.11183321000000006 * scale,
        0.1120438799999999 * scale,
        0.09291539000000006 * scale,
        0.1355426899999999 * scale,
        0.09291539000000006 * scale);
    gc.closePath();
    gc.beginStrokePath();
    gc.moveTo(0.62241591 * scale, 0.50023323 * scale);
    gc.splineTo(
        0.62241592 * scale,
        0.56789373 * scale,
        0.5675663099999999 * scale,
        0.62274336 * scale,
        0.49990578999999996 * scale,
        0.6227433499999999 * scale);
    gc.splineTo(
        0.43224528999999995 * scale,
        0.6227433499999999 * scale,
        0.37739566999999996 * scale,
        0.5678936499999999 * scale,
        0.37739567999999996 * scale,
        0.5002332299999999 * scale);
    gc.splineTo(
        0.37739567999999996 * scale,
        0.43257272999999985 * scale,
        0.43224527999999995 * scale,
        0.37772311999999986 * scale,
        0.49990578999999996 * scale,
        0.37772311999999986 * scale);
    gc.splineTo(
        0.5675662899999999 * scale,
        0.37772310999999986 * scale,
        0.62241591 * scale,
        0.43257271999999986 * scale,
        0.62241591 * scale,
        0.5002332299999999 * scale);
    gc.closePath();
    gc.beginStrokePath();
    gc.moveTo(0.57185662 * scale, 0.74684161 * scale);
    gc.lineTo(0.6699025200000001 * scale, 0.74684161 * scale);
    gc.lineTo(0.6699025200000001 * scale, 0.8917361300000001 * scale);
    gc.lineTo(0.57185662 * scale, 0.8917361300000001 * scale);
    gc.closePath();

    return out;
  }
}
