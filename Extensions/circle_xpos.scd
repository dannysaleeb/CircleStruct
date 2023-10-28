+ SimpleNumber {

	circle_xpos {
		arg axis_onset, angular_vel, time_step;
		var radius;
		radius = this - axis_onset;
		^radius * cos(angular_vel * time_step)
	}

}

// How to find radius, based on where the note starts? This could be interesting, but isn't for now.