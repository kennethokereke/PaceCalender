<?php
$response = array();
include 'db_connect.php';
include 'functions.php';

//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, true); //convert JSON into array

//Check for Mandatory parameters
if (isset($input['email']) && isset($input['password'])) {
    $email = $input['email'];
    $password = $input['password'];
    $query = "SELECT password_hash, salt FROM users WHERE email = ?";

    if ($stmt = $con->prepare($query)) {
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->bind_result($passwordHashDB, $salt);
        if ($stmt->fetch()) {
            //Validate the password
            if (password_verify(concatPasswordWithSalt($password, $salt), $passwordHashDB)) {
                $response["status"] = 0;
                $response["message"] = "Login successful";
            } else {
                $response["status"] = 1;
                $response["message"] = "Invalid email and password combination";
            }
        } else {
            $response["status"] = 1;
            $response["message"] = "Invalid email and password combination";
        }

        $stmt->close();
    }
} else {
    $response["status"] = 2;
    $response["message"] = "Missing mandatory parameters";
}
//Display the JSON response
echo json_encode($response);
