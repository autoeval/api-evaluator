package com.autoeval.api.evaluator.model.resume;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class ResumeResultEvaluationPredicates {

    public static <N, S> BiPredicate<ResumeMatchResponse, Double> confidenceScoreMatch () {
        return (n, s) -> {
            return n.getMetadata().getConfidenceScore() >= s;
        };
    }

    public static <N, S> BiPredicate<ResumeMatchResponse, Integer>  resumeCountMatch (){
        return (n, s) -> {
            return n.getCount() >= s;
        };
    }

    public static <N, S> BiPredicate<ResumeMatchResponse, Map<String, String>>  areTopResumes (){
        return (n, s) -> {
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            n.getResults().forEach(results -> {
                if (s.get(results.getFilepath()) == null)
                    atomicBoolean.set(false);
            });
            return atomicBoolean.get();
        };
    }
}
