package org.example.project.state

import org.example.project.db.ScoreStorage
import org.example.project.model.ScoreResult
import org.example.project.model.ScoreSource

class SolutionUseCase(
    private val storage: ScoreStorage,
    private val statisticComposer: StatisticComposer,
    private val baseStatistic: String
) {

    suspend operator fun invoke(): ScoreResult {
        val score = storage.getSavedScore()

        if (!score.isNullOrBlank()) {
            //            log("UseCase: link from DB = $score")
            return ScoreResult(
                score = score,
                source = ScoreSource.DATABASE
            )
        }

        val finalStatistic = statisticComposer.compose(baseStatistic)

        //        log("UseCase: final link built = $finalStatistic")

        return ScoreResult(
            score = finalStatistic,
            source = ScoreSource.BUILT
        )
    }
}