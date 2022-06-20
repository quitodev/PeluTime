package com.pelutime.application.injection

import com.pelutime.domain.main.account.AccountImpl
import com.pelutime.domain.main.account.IAccountRepo
import com.pelutime.domain.main.booking.BookingImpl
import com.pelutime.domain.main.booking.IBookingRepo
import com.pelutime.domain.main.employees.EmployeesImpl
import com.pelutime.domain.main.employees.IEmployeesRepo
import com.pelutime.domain.main.home.HomeImpl
import com.pelutime.domain.main.home.IHomeRepo
import com.pelutime.domain.login.ILoginRepo
import com.pelutime.domain.login.LoginImpl
import com.pelutime.domain.main.IMainRepo
import com.pelutime.domain.main.MainImpl
import com.pelutime.domain.main.maps.IMapsRepo
import com.pelutime.domain.main.maps.MapsImpl
import com.pelutime.domain.main.ubication.IUbicationRepo
import com.pelutime.domain.main.ubication.UbicationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(ActivityRetainedComponent::class)
@ExperimentalCoroutinesApi
abstract class ActivityModule {
    @Binds
    abstract fun bindLoginDataSource(loginImpl: LoginImpl) : ILoginRepo
    @Binds
    abstract fun bindMainDataSource(mainImpl: MainImpl) : IMainRepo
    @Binds
    abstract fun bindMapsDataSource(mapsImpl: MapsImpl) : IMapsRepo
    @Binds
    abstract fun bindHomeDataSource(homeImpl: HomeImpl) : IHomeRepo
    @Binds
    abstract fun bindBookingDataSource(bookingImpl: BookingImpl) : IBookingRepo
    @Binds
    abstract fun bindEmployeesDataSource(employeesImpl: EmployeesImpl) : IEmployeesRepo
    @Binds
    abstract fun bindAccountDataSource(accountImpl: AccountImpl) : IAccountRepo
    @Binds
    abstract fun bindUbicationDataSource(ubicationImpl: UbicationImpl) : IUbicationRepo
}
