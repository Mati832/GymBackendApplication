package adapter.in.controller;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/workouts")
public class WorkoutController {

    @POST
    public Response createWorkout(){
        //authentifizierung
        //usecase
        //hier dann presenter
        return null;
    }
    @Path("/{workoutID}")
    @POST
    public Response addExerciseToWorkout(){
        //authentifizierung und autorisierung ob der nutzer auch das workout erstellt hat.
        //presenter
        return null;
    }
}
