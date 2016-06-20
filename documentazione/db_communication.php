<?php

// Load Request
$api_method = isset($_POST['api_method']) ? $_POST['api_method'] : '';
$api_data = isset($_POST['api_data']) ? $_POST['api_data'] : '';

// Validate Request
if (empty($api_method) || empty($api_data)) {
    API_Response(true, 'Invalid Request');
}
if (!function_exists($api_method)) {
    API_Response(true, 'API Method Not Implemented');
}

// Call API Method
call_user_func($api_method, $api_data);

// Helper Function

function API_Response($isError, $errorMessage, $responseData = '')
{
    exit(json_encode(array(
        'IsError' => $isError,
        'ErrorMessage' => $errorMessage,
        'ResponseData' => $responseData
    )));
}

// Funzioni

function loginUser($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Login Data
		$login_data = json_decode($api_data);
		// Istruzione
		$query = "SELECT * FROM User";
		$result = mysql_query($query);
		while ($row = mysql_fetch_array($result)) 
		{ 
			if($row['mobile'] == $login_data->mobile){
				echo "ok";
			}
		}
	}
}

function logoutUser($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Login Data
		$logout_data = json_decode($api_data);
		// Istruzione
		$query = "SELECT * FROM User";
		$result = mysql_query($query);
		$errore = false;
		while ($row = mysql_fetch_array($result)) 
		{ 
			if($row['mobile'] == $logout_data->mobile){
				if("SELECT count(*) FROM User_Position WHERE User_ID = '".$row['id']"'"){
					$query = "DELETE * FROM User_Position WHERE User_ID = '".$row['id']"'";
					if(!mysql_query($query)){
						API_Response(true,"Errore nel logout dell'utente");
						$errore = true;
					}
				}
				if("SELECT count(*) FROM User_City WHERE User_ID = '".$row['id']"'"){
					$query = "DELETE * FROM User_City WHERE User_ID = '".$row['id']"'";
					if(!mysql_query($query)){
						API_Response(true,"Errore nel logout dell'utente");
						$errore = true;
					}
				}
				if("SELECT count(*) FROM User_Type WHERE User_ID = '".$row['id']"'"){
					$query = "DELETE * FROM User_Type WHERE User_ID = '".$row['id']"'";
					if(!mysql_query($query)){
						API_Response(true,"Errore nel logout dell'utente");
						$errore = true;
					}
				}
				if($errore)
					echo "ok";
			}
		}
	}
}
/*
function deleteUser($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if ($conn == FALSE)
		die ("Errore nella connessione.");
    $ris = mysql_select_db($db_name);
    if ($ris == FALSE)
		die ("Errore nella selezione del DB.");
    // Decode Login Data
    $login_data = json_decode($api_data);
	// Istruzione
  	$query = "DELETE FROM users WHERE mobile = '".$login_data->mobile."'";
	$result = mysql_query($query);
    if($result == TRUE)
      	echo "Delete success!";
}
*/
function registerUser($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Register Data
		$login_data = json_decode($api_data);
		// Istruzione
		$query = "INSERT INTO User (Name, Surname, Mobile, Password) VALUES ('".$login_data->name."','".$login_data->surname."','".$login_data->mobile."','".$login_data->password."')";
		$result = mysql_query($query);
		if($result == TRUE)
			echo "ok";
	}
}

function checkUniqueness($api_data){
	$db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Register Data
		$login_data = json_decode($api_data);
		// Istruzione
		$query = "SELECT count(*) FROM User WHERE mobile = '".$login_data->mobile."'";
		if(mysql_query($query)=='0'){
			echo "ok";
		}
	}
}

function User_Type($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Register Data
		$data = json_decode($api_data);
		// Istruzione
		$query = "INSERT INTO User_Type (User_ID,Type_ID) VALUES ('".$data->id."','".$data->type."')";
		$result = mysql_query($query);
		if($result == TRUE)
			echo "ok";
	}
}

function User_City($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Register Data
		$data = json_decode($api_data);
		// Istruzione
		$query = "INSERT INTO User_City (User_ID,City_ID) VALUES ('".$data->id."','".$data->city."')";
		$result = mysql_query($query);
		if($result == TRUE)
			echo "ok";
	}
}

function Position($api_data)
{
    $db_name = "";
    $db_user = "";
    $db_host = "";
    $conn = mysql_connect($db_host, $db_user);
    if(checkConnection($conn,$db_name)){
		// Decode Register Data
		$data = json_decode($api_data);
		// Istruzione
		$query = "INSERT INTO User_Postition (User_ID,Latitude,Longitude) VALUES ('".$data->id."','".$data->latitude."','".$data->longitude."')";
		$result = mysql_query($query);
		if($result == TRUE)
			echo "ok";
	}
}

function checkConnection($conn,$db_name){
	$errore = false;
	if ($conn == FALSE)
	{
		API_Response(true,"Errore nella connessione.");
		$errore = true;
	}
    $ris = mysql_select_db($db_name);
    if ($ris == FALSE)
	{
		API_Response(true,"Errore nella connessione col database.");
		$errore = true;
	}
	return $errore;
}
?>