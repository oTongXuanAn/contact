package an.xuan.tong.historycontact.repository

class HistoryException(message: String?, cause: Throwable?) : Throwable(message, cause) {
    constructor(message: String?) : this(message, null)
    constructor(cause: Throwable?) : this(null, cause)
    constructor() : this(null, null)
}