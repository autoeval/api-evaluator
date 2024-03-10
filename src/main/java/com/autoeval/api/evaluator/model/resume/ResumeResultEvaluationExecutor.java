package com.autoeval.api.evaluator.model.resume;

import com.autoeval.api.evaluator.model.ConditionExecutor;
import com.autoeval.api.evaluator.model.resume.ResumeMatchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class ResumeResultEvaluationExecutor implements ConditionExecutor {

    Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .disableHtmlEscaping()
            .create();
    Map<String, String> prompt1Map = Map.of(
            "15651486.pdf","0.9231288433074951",
            "66832845.pdf","0.9208971261978149",
            "17641670.pdf","0.9203760623931885",
            "23864648.pdf","0.9189959168434143",
            "22450718.pdf","0.9179288148880005",
            "24913648.pdf","0.9154694080352783",
            "10089434.pdf","0.9147783517837524",
            "26480367.pdf","0.9133636355400085",
            "29051656.pdf","0.9129638075828552",
            "17688766.pdf","0.9126716256141663"
    );

    Map<String, String> prompt2Map = Map.of(
            "24402267.pdf","0.9091587066650391",
            "41523474.pdf","0.906809389591217",
            "14640322.pdf","0.9065560698509216",
            "25724495.pdf","0.9056178331375122",
            "27165830.pdf","0.905105710029602",
            "25676643.pdf","0.9040544033050537",
            "16877897.pdf","0.9040147066116333",
            "87968870.pdf","0.903924822807312",
            "23155093.pdf","0.9036393165588379"
    );

    Map<String, String> prompt3Map = Map.of(
            "15011085.pdf, ","0.9103233814239502",
            "98513424.pdf, ","0.9094909429550171",
            "12858898.pdf, ","0.9083854556083679",
            "81677620.pdf, ","0.9063771963119507",
            "24833063.pdf, ","0.9058389067649841",
            "58165257.pdf, ","0.9043728113174438",
            "39675895.pdf, ","0.9036420583724976",
            "15891494.pdf, ","0.9025589227676392",
            "76454959.pdf, ","0.9022632837295532",
            "27330027.pdf, ","0.9016106128692627"
        );

    public boolean apply(final String responseText) {
        ResumeMatchResponse resumeMatchResponse = gson.fromJson(responseText, ResumeMatchResponse.class);
        return (ResumeResultEvaluationPredicates.confidenceScoreMatch().test(resumeMatchResponse, 0.7) &&
                ResumeResultEvaluationPredicates.resumeCountMatch().test(resumeMatchResponse, 3) &&
                ResumeResultEvaluationPredicates.areTopResumes().test(resumeMatchResponse, prompt1Map));

    }

}
