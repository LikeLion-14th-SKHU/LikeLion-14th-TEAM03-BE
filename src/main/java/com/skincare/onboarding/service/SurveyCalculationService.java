package com.skincare.onboarding.service;

import com.skincare.onboarding.dto.SurveyAnswerDto;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyCalculationService {

    private static final double HIGH = 0.55;

    public SurveyResult calculate(Onboarding onboarding, SurveyAnswerDto s, String concernRaw) {

        // 1. H1 가산점 계산
        double acneBonus = 0, sensBonus = 0, rednessBonus = 0;
        List<String> h1 = s.getH1();
        if (h1 != null) {
            for (String diagnosis : h1) {
                switch (diagnosis) {
                    case "acne"    -> acneBonus += 2;
                    case "rosacea" -> { sensBonus += 3; rednessBonus += 2; }
                    case "atopy", "contact", "eczema" -> sensBonus += 3;
                }
            }
        }

        // 2. F1 스킵 로직
        int f1 = s.getF1();
        int f2 = (f1 == 1 || s.getF2() == null) ? 1 : s.getF2();

        // 3. 축별 합산
        double sebum   = s.getA1() + s.getA2() + s.getA3() + s.getA4();
        double hydra   = s.getB1() + s.getB2() + s.getB3();
        double sens    = s.getD1() + s.getD2() + sensBonus;
        double redness = s.getE1() + s.getE2() + rednessBonus;
        double acne    = f1 + f2 + acneBonus;
        double mark    = s.getG1();

        // 4. 정규화
        double sebumN = (sebum - 4) / 12.0;
        double hydraN = (hydra - 3) / 9.0;

        // 5. 피부타입 판정 (C1 최우선)
        String baseType;
        boolean zoneDiff = "zone_diff_true".equals(s.getC1());
        if (zoneDiff) {
            baseType = "COMBO";
        } else if (sebumN >= HIGH && hydraN < HIGH) {
            baseType = "OILY";
        } else if (sebumN < HIGH && hydraN >= HIGH) {
            baseType = "DRY";
        } else if (sebumN >= HIGH && hydraN >= HIGH) {
            baseType = "OILY";
        } else {
            baseType = "NORMAL";
        }

        // 6. 중첩속성
        boolean dehydrated = sebumN >= HIGH && hydraN >= HIGH;
        boolean sensitive  = sens >= 5;
        boolean acneFlag   = acne >= 5;
        boolean markProne  = mark >= 3;

        // 7. 안전 플래그
        boolean onMedication = !"none".equals(s.getH2());
        boolean inflammatory = f2 == 4;

        // 8. trouble_scores 10점 환산 + Math.round 추가 ✅
        double tsSebum   = Math.round(((s.getA1() + s.getA2() - 2) / 6.0 * 9 + 1) * 10.0) / 10.0;
        double tsPore    = Math.round(((s.getA3() + s.getA4() - 2) / 6.0 * 9 + 1) * 10.0) / 10.0;
        double tsDryness = Math.round(((s.getB1() - 1) / 3.0 * 9 + 1) * 10.0) / 10.0;
        double tsRedness = Math.round(((s.getE1() + s.getE2() - 2) / 6.0 * 9 + 1) * 10.0) / 10.0;
        double tsAcne    = Math.round(((f1 + f2 - 2) / 6.0 * 9 + 1) * 10.0) / 10.0;
        double tsMark    = Math.round(((s.getG1() - 1) / 3.0 * 9 + 1) * 10.0) / 10.0;

        return SurveyResult.builder()
                .onboarding(onboarding)
                .baseType(baseType)
                .dehydrated(dehydrated)
                .sensitive(sensitive)
                .acneFlag(acneFlag)
                .markProne(markProne)
                .onMedication(onMedication)
                .retinolHistory(s.getH3())
                .inflammatory(inflammatory)
                .sebumRaw(sebum)
                .hydraRaw(hydra)
                .sensRaw(sens)
                .rednessRaw(redness)
                .acneRaw(acne)
                .markRaw(mark)
                .tsSebum(tsSebum)
                .tsPore(tsPore)
                .tsDryness(tsDryness)
                .tsRedness(tsRedness)
                .tsAcne(tsAcne)
                .tsMark(tsMark)
                .concernRaw(concernRaw != null ? concernRaw : "")
                .build();
    }
}