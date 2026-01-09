package adapter.in.controller;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/exercises")
public class ExerciseController {

    @POST
    public Response createExercise(){
        //authentifizierung
        //usecase
        //hier presenter
        return null;
    }
}
