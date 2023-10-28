CircleStruct {
	var <stream, <timesteps, <deltaVals, <duration, <axis_onset, <>current_timestep=0;

	*new {
		arg stream, timesteps=64;
		var deltaVals=Array.new(), duration=0, axis_onset;

		// Parse Pbind to array of events
		stream = Pevent(stream).asStream.convertArray;

		stream.do({
			arg event;
			deltaVals = deltaVals.add(event.delta)
		});

		// add abs_onsets to events
		stream.do({
			arg event, i;
			event.add(\abs_onset -> deltaVals.abs_onsets[i])
		});

		// calculate stream duration -- maybe should be using delta?
		stream.do({
			arg event;
			duration = duration + event.delta
		});

		// calculate axis_onset
		axis_onset = duration / 2;

		^super.newCopyArgs(stream, timesteps, deltaVals, duration, axis_onset)
	}

	toStream {
		arg event_array;
		^Routine({
			event_array.do({
				arg event;
				event.yield
			})
		}).asStream
	}

	calculate_position {
		// get onsets at some rotational position
		arg timestep;
		var stream_copy;

		// make copy of stream
		stream_copy = Array.new();
		this.stream.do({
			arg event, i;
			stream_copy = stream_copy.add(event.copy)
		});

		// calculate new abs_onset for every event based on changing timestep and sort by abs_onsets
		stream_copy.do({
			arg event, i;
			stream_copy[i].abs_onset = this.stream[i].abs_onset.circle_xpos(this.axis_onset, 2pi / this.timesteps, timestep).round(0.01)
		}).sort({ arg a, b; a.abs_onset < b.abs_onset});

		// calculate and update delta values
		(stream_copy.size - 1).do({
			arg i;
			stream_copy[i][\delta] = stream_copy[i+1].abs_onset - stream_copy[i].abs_onset
		});

		^this.toStream(stream_copy);
	}

	set_position {
		arg timestep;

		this.current_timestep = timestep;
		^this.calculate_position(timestep);
	}

	get_angles {
		// make a calculation for visual work ...
	}

	next {
		// return position at next timestep
		this.current_timestep = this.current_timestep + 1;
		^this.calculate_position(this.current_timestep)
	}

	previous {
		// return position at previous timestep
		this.current_timestep = this.current_timestep - 1;
		^this.calculate_position(this.current_timestep)
	}

}

// Ok so CircleStruct takes a Pbind, and outputs a Routine