<?php
// Create tokens
$selector = bin2hex(random_bytes(8));
$token = random_bytes(32);

$url = sprintf('%sreset.php?%s', ABS_URL, http_build_query([
    'selector' => $selector,
    'validator' => bin2hex($token)
]));

// Token expiration
$expires = new DateTime('NOW');
$expires->add(new DateInterval('PT01H')); // 1 hour

// Delete any existing tokens for this user
$this->db->delete('password_reset', 'email', $user->email);

// Insert reset token into database
$insert = $this->db->insert('password_reset',
    array(
        'email'     =>  $user->email,
        'selector'  =>  $selector,
        'token'     =>  hash('sha256', $token),
        'expires'   =>  $expires->format('U'),
    )
);

// Send the email
// Recipient
$to = $user->email;

// Subject
$subject = 'Your password reset link';

// Message
$message = '<p>We recieved a password reset request. The link to reset your password is below. ';
$message .= 'If you did not make this request, you can ignore this email</p>';
$message .= '<p>Here is your password reset link:</br>';
$message .= sprintf('<a href="%s">%s</a></p>', $url, $url);
$message .= '<p>Thanks!</p>';

// Headers
$headers = "From: " . ADMIN_NAME . " <" . ADMIN_EMAIL . ">\r\n";
$headers .= "Reply-To: " . ADMIN_EMAIL . "\r\n";
$headers .= "Content-type: text/html\r\n";

// Send email
$sent = mail($to, $subject, $message, $headers);


// Send the email
// Recipient
$to = $user->email;

// Subject
$subject = 'Your password reset link';

// Message
$message = '<p>We recieved a password reset request. The link to reset your password is below. ';
$message .= 'If you did not make this request, you can ignore this email</p>';
$message .= '<p>Here is your password reset link:</br>';
$message .= sprintf('<a href="%s">%s</a></p>', $url, $url);
$message .= '<p>Thanks!</p>';

// Headers
$headers = "From: " . ADMIN_NAME . " <" . ADMIN_EMAIL . ">\r\n";
$headers .= "Reply-To: " . ADMIN_EMAIL . "\r\n";
$headers .= "Content-type: text/html\r\n";

// Send email
$sent = mail($to, $subject, $message, $headers);


// Check for tokens
$selector = filter_input(INPUT_GET, 'selector');
$validator = filter_input(INPUT_GET, 'validator');

// Get tokens
$results = $this->db->get_results("SELECT * FROM password_reset WHERE selector = :selector AND expires >= :time", ['selector'=>$selector,'time'=>time()]);

if ( empty( $results ) ) {
    return array('status'=>0,'message'=>'There was an error processing your request. Error Code: 002');
}

$auth_token = $results[0];
$calc = hash('sha256', hex2bin($validator));

// Validate tokens
if ( hash_equals( $calc, $auth_token->token ) )  {
    $user = $this->user_exists($auth_token->email, 'email');

    if ( false === $user ) {
        return array('status'=>0,'message'=>'There was an error processing your request. Error Code: 003');
    }

    // Update password
    $update = $this->db->update('users',
        array(
            'password'  =>  password_hash($password, PASSWORD_DEFAULT),
        ), $user->ID
    );

    // Delete any existing password reset AND remember me tokens for this user
    $this->db->delete('password_reset', 'email', $user->email);
    $this->db->delete('auth_tokens', 'username', $user->username);

    if ( $update == true ) {
        // New password. New session.
        session_destroy();

        return array('status'=>1,'message'=>'Password updated successfully. <a href="index.php">Login here</a>');
    }
}
