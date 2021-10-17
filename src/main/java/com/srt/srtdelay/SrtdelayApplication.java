package com.srt.srtdelay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SrtdelayApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SrtdelayApplication.class, args);

		if (args.length > 0) {
			System.out.println(args[0]);

		InputStream inputStream = new FileInputStream(args[0]);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		OutputStream outPutstream = new FileOutputStream("WithOneSecDelay" + args[0] + ".srt");
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outPutstream);
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

		String s = bufferedReader.readLine();
		String rgx = "[0-9][0-9]:[0-9][0-9]:[0-9][0-9],[0-9][0-9][0-9]\\s-->\\s[0-9][0-9]:[0-9][0-9]:[0-9][0-9],[0-9][0-9][0-9]";
		Pattern pattern = Pattern.compile(rgx);
		while (s != null) {
			Matcher matcher = pattern.matcher(s);

			if (matcher.matches()) {

				String newHourMinSec = new SrtdelayApplication().getNewHourMinSec(s);
				bufferedWriter.write(newHourMinSec);
			} else {
				bufferedWriter.write(s);
			}
			bufferedWriter.write("\r\n");

			s = bufferedReader.readLine();
		}

		bufferedReader.close();
		bufferedWriter.close();

		} else {
			System.out.println("FileName not found on args[0]!!!");
		}
	}

	public String getNewHourMinSec(String s) {

		String[] primeiroSplit = s.split("\\s+");
		String[] enableTimeArray = primeiroSplit[0].split(",");
		String enableTime = getHourMinuteSecondsParcial(enableTimeArray[0]);
		enableTime = enableTime + "," + enableTimeArray[1];

		String[] disableTimeArray = primeiroSplit[2].split(",");
		String disableTime = getHourMinuteSecondsParcial(disableTimeArray[0]);
		disableTime = disableTime + "," + disableTimeArray[1];
		return enableTime + " --> " + disableTime;
	}

	private String getHourMinuteSecondsParcial(String timeSplit) {

		String[] hourMinuteSeconds = timeSplit.split(":");

		int hour = Integer.parseInt(hourMinuteSeconds[0]);
		int min = Integer.parseInt(hourMinuteSeconds[1]);
		int sec = Integer.parseInt(hourMinuteSeconds[2]);
		sec++;

		if (sec > 59) {
			sec = 0;
			min++;
			if (min > 59) {
				min--;
				// TODO   when min++ is more than 59 min ==0 and hour++ 
			}

		}
		hourMinuteSeconds[0] = String.format("%02d", hour);
		hourMinuteSeconds[1] = String.format("%02d", min);
		hourMinuteSeconds[2] = String.format("%02d", sec);

		return hourMinuteSeconds[0] + ":" + hourMinuteSeconds[1] + ":" + hourMinuteSeconds[2];
	}

}
