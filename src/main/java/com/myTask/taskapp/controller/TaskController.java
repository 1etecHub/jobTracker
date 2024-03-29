package com.myTask.taskapp.controller;

import com.myTask.taskapp.dto.requestDto.TaskRequest;
import com.myTask.taskapp.dto.responseDto.ApiResponse;
import com.myTask.taskapp.dto.responseDto.TaskResponse;
import com.myTask.taskapp.entity.Task;
import com.myTask.taskapp.exceptions.CommonApplicationException;
import com.myTask.taskapp.security.JWTService;
import com.myTask.taskapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-task/")
@Slf4j
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final JWTService jwtService;



    @PostMapping("/create-task")
    public ResponseEntity<ApiResponse> createTask(@Valid @RequestBody TaskRequest request,
                                                  @RequestHeader("Authorization") String authorizationHeader)
            throws CommonApplicationException {
        var userDetails=jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("request for employer  {} to create task");
        ApiResponse apiResponse = taskService.createTask(request, (String) userDetails.get("email"));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/assign-order-to-deliveryman/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTaskToEmployee
            (@RequestBody TaskRequest request,
             @PathVariable Long taskId,
              @RequestHeader("Authorization") String authorizationHeader) throws CommonApplicationException {
        var userDetails= jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Employer with ID:: " + userDetails.get("email")+" wants to assign task to a user");
        ApiResponse<TaskResponse> response = taskService.assignTaskToEmployee(userDetails.get("email"), taskId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    @PutMapping("/update-task/{taskId}")
    public ResponseEntity<ApiResponse<Task>> updateTask(@RequestBody TaskRequest request,
                                                        @PathVariable Long taskId,
                                                        @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonApplicationException {
        var userDetails=jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Employer with ID:: " + userDetails.get("email")+" wants to update the details of a task");
        ApiResponse<Task> updated = taskService.updateTask(taskId, request, (String) userDetails.get("email"));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @GetMapping("/get-tasks/")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTaskByEmployee(@RequestHeader("Authorization") String authorizationHeader)
            throws CommonApplicationException {
        log.info("Received request with Authorization Header: {}", authorizationHeader);
        var userDetails=jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Request for user {} to get all created tasks", userDetails.get("name"));
        String userEmail = userDetails.get("email");
        ApiResponse<List<TaskResponse>> taskList = taskService.getAllAssignedTaskByEmployee(userEmail);
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }

    @GetMapping("/get-tasks/")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTaskByEmployer(@RequestHeader("Authorization") String authorizationHeader)
            throws CommonApplicationException {
        log.info("Received request with Authorization Header: {}", authorizationHeader);
        var userDetails=jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Request for user {} to get all created tasks", userDetails.get("name"));
        String userEmail = userDetails.get("email");
        ApiResponse<List<TaskResponse>> taskList = taskService.getAllTaskByEmployer(userEmail);
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }



    /*@GetMapping("/get-task/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable Long taskId)
            throws CommonApplicationException {
        log.info("Received request with Authorization Header: {}", authorizationHeader);
        var userDetails=jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Request for user {} to get all created tasks", userDetails.get("name"));
        String userEmail = userDetails.get("email");
        ApiResponse<TaskResponse> task = taskService.getTaskByUser(userEmail, taskId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }*/



    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<ApiResponse<String>> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws CommonApplicationException {
        log.info("Received request with Authorization Header: {}", authorizationHeader);
        var userDetails = jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Request for employee {} to delete a task", userDetails.get("name"));
        String userEmail = userDetails.get("email");
        ApiResponse<String> apiResponse = taskService.deleteTask(taskId, userEmail);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PutMapping("/update-task-status/{taskId}")
    public ResponseEntity<ApiResponse<Task>> updateTaskStatus(@RequestParam String status,
                                                              @PathVariable Long taskId,
                                                              @RequestHeader("Authorization") String authorizationHeader)
            throws CommonApplicationException {
        var userDetails = jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("User {} is updating task status for task id {}", userDetails.get("name"), taskId);

        ApiResponse<Task> updated = taskService.updateTaskStatus(taskId, status, (String) userDetails.get("email"));
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }



    /*@GetMapping("/get-tasks/")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTask(@RequestHeader("Authorization") String authorizationHeader)
            throws CommonApplicationException {
        log.info("Received request with Authorization Header: {}", authorizationHeader);
        var userDetails=jwtService.validateTokenAndReturnDetail(authorizationHeader.substring(7));
        log.info("Request for employer {} to get all created tasks", userDetails.get("name"));
        String userEmail = userDetails.get("email");
        ApiResponse<List<TaskResponse>> taskList = taskService.getAllTaskByEmployer(userEmail);
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }*/

}
