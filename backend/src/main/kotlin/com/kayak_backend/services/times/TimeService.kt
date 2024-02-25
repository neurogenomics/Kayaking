package com.kayak_backend.services.times

import java.time.LocalDateTime

interface TimeService {
    fun getTimes(): List<LocalDateTime>
}
