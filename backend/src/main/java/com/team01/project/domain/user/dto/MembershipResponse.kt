import java.time.LocalDate

data class MembershipResponse(
    val grade: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val autoRenew: Boolean
)
