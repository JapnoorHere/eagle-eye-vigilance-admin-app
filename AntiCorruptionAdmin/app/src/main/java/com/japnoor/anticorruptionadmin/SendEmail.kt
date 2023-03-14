package com.japnoor.anticorruptionadmin

import papaya.`in`.sendmail.SendMail

class SendEmail {
    fun sendComplaint(
        userEmail: String,
        userName: String,
        complaintNumber: String,
        complaintAgainst: String,
        complaintDetail: String,
        suspectsDept: String,
        suspectCategory: String,
        suspectLoc: String,
        userDistrict: String,
        audio: String,
        video: String
    ) {
        when (userDistrict) {
            "Amritsar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "code2decoding@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Barnala" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "mehrchandcse544@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Bathinda" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2021appscicomp@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Faridkot" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "s2.abhijeet@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Fatehgarh Sahib" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydraiscrazy2004@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Firozpur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydracg6@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Fazilka" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp502@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Gurdaspur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp527@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Hoshiarpur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "todolistsync@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Jalandhar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp544@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Kapurthala" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp527@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Ludhiana" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp523@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Malerkotla" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "code2decoding@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Mansa" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp544@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Moga" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "todolistsync@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Sri Muktsar Sahib" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp527@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Pathankot" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydracg6@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Patiala" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydraiscrazy2004@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Rupnagar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "code2decoding@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Sahibzada Ajit Singh Nagar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "mehrchandcse544@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Sangrur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "mehrchandcse544@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Shahid Bhagat Singh Nagar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "s2.abhijeet@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }
            "Taran Taran" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp528@gmail.com",
                    "Complaint Against : ${complaintAgainst}\n",
                    "The Complaint has been accepted\n" +
                            "1. Complaint Submitted by : ${userName}\n" +
                            "2. Complaint Submitted by (Email) : ${userEmail}\n" +
                            "3. Complaint Number : ${complaintNumber}\n" +
                            "4. Complaint Details : ${complaintDetail}\n" +
                            "5. Suspects Department : ${suspectsDept}\n" +
                            "6. Suspects Category : ${suspectCategory}\n" +
                            "7. Suspects Location : ${suspectLoc}\n" +
                            "8. User District : ${userDistrict}\n" +
                            "9. Complaint Audio : ${audio}\n" +
                            "10. Complaint Video : ${video}\n"
                )
                mail.execute()
            }

        }

    }


    fun sendDemand(
        userName: String,
        userEmail: String,
        userDistrict: String,
        demandNumber: String,
        demandSubject: String,
        demandDetails: String,
        demandUnion: String,
        demandImage: String
    ) {



        when (userDistrict) {
            "Amritsar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "code2decoding@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Barnala" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "mehrchandcse544@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Bathinda" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2021appscicomp@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Faridkot" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "s2.abhijeet@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Fatehgarh Sahib" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydraiscrazy2004@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Firozpur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydracg6@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Fazilka" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp502@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Gurdaspur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp527@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Hoshiarpur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "todolistsync@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Jalandhar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp544@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Kapurthala" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp527@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Ludhiana" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp523@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Malerkotla" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "code2decoding@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Mansa" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp544@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Moga" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "todolistsync@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Sri Muktsar Sahib" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp527@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Pathankot" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydracg6@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Patiala" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "hydraiscrazy2004@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Rupnagar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "code2decoding@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Sahibzada Ajit Singh Nagar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "mehrchandcse544@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Sangrur" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "mehrchandcse544@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Shahid Bhagat Singh Nagar" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "s2.abhijeet@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }
            "Taran Taran" -> {
                val mail = SendMail(
                    "eagleeyevigilance@gmail.com",
                    "kopbxcdsbqqzaldq",
                    "2020appscicomp528@gmail.com",
                    "Demand Letter of : ${demandSubject}\n",
                    "This Demand Letter has been accepted\n" +
                            "1. Demand Letter Submitted by : ${userName}\n" +
                            "2. Demand Letter Submitted by (Email) : ${userEmail}\n" +
                            "3. Demand Number : ${demandNumber}\n" +
                            "4. Demand Letter Details : ${demandDetails}\n" +
                            "5. Demand Letter Union : ${demandUnion}\n" +
                            "6. User District : ${userDistrict}\n" +
                            "7. Demand Letter Image : ${demandImage}\n"
                )
                mail.execute()
            }

        }
    }

}