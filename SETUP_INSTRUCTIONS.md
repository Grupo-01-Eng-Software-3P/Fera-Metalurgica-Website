# Resolução do Erro: TypeTag :: UNKNOWN

## Problema
Ao clicar em "Run" na IDE JetBrains, aparece o erro:
```
java: java.lang.ExceptionInInitializerError
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Solução

### Passo 1: Invalidar Cache da IDE
1. Abra a IDE JetBrains (IntelliJ IDEA)
2. Vá em **File → Invalidate Caches and Restart**
3. Selecione **"Invalidate and Restart"**
4. Aguarde a IDE reiniciar (pode levar 1-2 minutos)

### Passo 2: Recarregar Projeto Maven
1. Após a IDE reiniciar, vá em **View → Tool Windows → Maven**
2. No painel Maven à direita, clique com botão direito na raiz do projeto "metalurgica"
3. Selecione **"Reload Projects"**
4. Aguarde a recarga completar

### Passo 3: Configurar o Compilador da IDE
1. Vá em **File → Project Structure**
2. Na aba **Project Settings → Project**:
   - Certifique-se de que **SDK** está configurado como "21" (Java 21)
   - Certifique-se de que **Language level** está configurado como "21"
3. Clique em **Apply** e **OK**

### Passo 4: Limpar Arquivos Compilados
1. Vá em **Build → Clean Project**
2. Depois vá em **Build → Build Project**

## Se ainda não funcionar

Execute no terminal:
```bash
mvn clean install -DskipTests
```

Depois tente novamente:
1. File → Invalidate Caches and Restart
2. Aguarde reiniciar
3. Clique em **Run**

## Informações do Projeto
- Java Version: 21
- Maven Version: 3.9.14
- Spring Boot: 3.3.5
- Lombok: 1.18.30

O arquivo `pom.xml` já foi atualizado com:
- Configuração correcta do `maven-compiler-plugin`
- Argumento `-XDignore.symbol.file` para evitar conflitos com Lombok
- Todas as dependências compatíveis com Java 21

