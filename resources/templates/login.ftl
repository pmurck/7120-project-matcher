<#-- @ftlvariable name="errors" type="java.util.Map<String,String>" -->
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <title>Project Matcher</title>

    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap.css" rel="stylesheet">


    <style>
        html,
        body {
            height: 100%;
        }

        body {
            display: -ms-flexbox;
            display: flex;
            -ms-flex-align: center;
            align-items: center;
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #f5f5f5;
        }

        .form-signin {
            width: 100%;
            max-width: 330px;
            padding: 15px;
            margin: auto;
        }
        .form-signin .checkbox {
            font-weight: 400;
        }
        .form-signin .form-control {
            position: relative;
            box-sizing: border-box;
            height: auto;
            padding: 10px;
            font-size: 16px;
        }
        .form-signin .form-control:focus {
            z-index: 2;
        }
        .form-signin input[type="text"] {
            margin-bottom: -1px;
            border-bottom-right-radius: 0;
            border-bottom-left-radius: 0;
        }
        .form-signin input[type="password"] {
            margin-bottom: 10px;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
        }
    </style>
</head>
<body class="text-center">
<form class="form-signin <#if errors?has_content>was-validated</#if>" method="post">
    <h1 class="h3 mb-3 font-weight-normal">Project Matcher</h1>
    <label for="inputUsername" class="sr-only">Usuario</label>
    <input type="text" id="inputUsername" class="form-control" placeholder="Usuario" name="username" required autofocus>
    <label for="inputPassword" class="sr-only">Contraseña</label>
    <input type="password" id="inputPassword" class="form-control" placeholder="Contraseña" name="password" required>
    <#if errors.invalid_login?has_content>
        <div class="invalid-feedback">${errors.invalid_login}</div>
    </#if>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Iniciar Sesión</button>
    <a href="/register">Sin usuario? Registrarse</a>
</form>
</body>
</html>
