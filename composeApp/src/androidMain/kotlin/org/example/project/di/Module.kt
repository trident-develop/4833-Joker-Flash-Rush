package org.example.project.di

import org.example.project.db.NotifyPrefs
import org.example.project.db.ScoreDao
import org.example.project.db.ScoreDbHelper
import org.example.project.db.ScoreStorage
import org.example.project.state.SolutionUseCase
import org.example.project.state.StatisticComposer
import org.example.project.state.StatisticParamsResolver
import org.example.project.utils.ShiftCodec
import org.example.project.utils.ShiftCodec.DM
import org.example.project.viewmodel.LoadingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single { ScoreDbHelper(get()) }
    single { ScoreDao(get()) }
    single { ScoreStorage(get()) }
    single { NotifyPrefs(get()) }

    single {
        StatisticParamsResolver(
            context = get()
        )
    }

    single { StatisticComposer(get()) }

    single {
        SolutionUseCase(
            storage = get(),
            statisticComposer = get(),
            baseStatistic = "${ShiftCodec.decode(DM)}/fudlix7bo3"
        )
    }

    viewModelOf(::LoadingViewModel)
}