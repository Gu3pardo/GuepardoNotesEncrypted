package guepardoapps.guepardonotesencrypted.helper;

public class PasswordStrengthHelper {

	public static final int MIN_PASSWORD_LENGTH = 8;

	public static boolean CheckValidity(String enteredPassword) {

		int charBigCount = 0;
		int charSmallCount = 0;
		int numberCount = 0;
		int signCount = 0;

		for (char entry : enteredPassword.toCharArray()) {
			if (entry >= '0' && entry <= '9') {
				numberCount++;
			} else if (entry >= 'a' && entry <= 'z') {
				charSmallCount++;
			} else if (entry >= 'A' && entry <= 'Z') {
				charBigCount++;
			} else {
				signCount++;
			}
		}

		if (charBigCount > 0) {
			if (charSmallCount > 0) {
				if (numberCount > 0) {
					if (signCount > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
