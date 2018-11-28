<?php
$response = array();
include 'db_connect.php';
include 'functions.php';

//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, true); //convert JSON into array

//Check for Mandatory parameters
if (isset($input['email']) && isset($input['password']) && isset($input['full_name']) && isset($input['user_type'])) {
    $email = $input['email'];
    $password = $input['password'];
    $fullName = $input['full_name'];
    $userType = $input['user_type'];

    //Check if user already exist
    if (!userExists($email)) {
        //Get a unique Salt
        $salt         = getSalt();

        //Generate a unique password Hash
        $passwordHash = password_hash(concatPasswordWithSalt($password, $salt), PASSWORD_DEFAULT);

        //Query to register new user
        $insertQuery  = "INSERT INTO users(email, full_name, user_type, password_hash, salt) VALUES (?,?,?,?,?)";
        if ($stmt = $con->prepare($insertQuery)) {
            $stmt->bind_param("sssss", $email, $fullName, $userType, $passwordHash, $salt);
            $stmt->execute();
            $response["status"] = 0;
            $response["message"] = "User created";
            $stmt->close();
        }
    } else {
        $response["status"] = 1;
        $response["message"] = "User exists";
    }
} else {
    $response["status"] = 2;
    $response["message"] = "Missing mandatory parameters";
}
echo json_encode($response);
