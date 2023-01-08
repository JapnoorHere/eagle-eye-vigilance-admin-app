package com.japnoor.anticorruptionadmin

import com.japnoor.anticorruptionadmin.demand.DemandLetter

interface ComplaintClickedInterface {
    fun onComplaintsClicked(complaints: Complaints)
    }
interface DemandClick {
    fun onDemandClick(demandLetter: DemandLetter)
    }
interface UsersClick {
    fun onUsersClick(users: Users)
    }
