<#-- @ftlvariable name="errors" type="java.util.Map<String,String>" -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <title>Project Matcher</title>
    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand navbar-dark bg-dark mb-4">
    <a class="navbar-brand" href="/">Project Matcher</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarCollapse">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item active">
                <a class="nav-link" href="/">Home</a>
            </li>
        </ul>
    </div>
</nav>
<main role="main" class="container">
    <div class="jumbotron">
        <h1>Registrarse</h1>
        <form method="post">
            <div class="form-row">
                <div class="form-group col-md-6">
                    <label for="firstName">Nombre</label>
                    <input type="text" class="form-control" id="firstName" name="firstName" placeholder="Nombre" required>
                </div>
                <div class="form-group col-md-6">
                    <label for="lastName">Apellido</label>
                    <input type="text" class="form-control" id="lastName" name="lastName" placeholder="Apellido">
                </div>
            </div>
            <div class="form-group <#if errors?has_content>was-validated</#if>">
                <label for="username">Nombre de usuario</label>
                <input type="text" class="form-control" id="username" name="username" placeholder="Usuario" required>
                <#if errors.existing_username?has_content>
                    <div class="invalid-feedback">${errors.existing_username}</div>
                </#if>
            </div>
            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Contraseña" required>
            </div>
            <button type="submit" class="btn btn-primary">Crear usuario</button>
        </form>
    </div>
</main>
</html>

