# Como rodar o projeto (guia para iniciantes)

Este guia assume que você **nunca usou Docker nem terminal antes**. Se você já
tem experiência, use o `README.md` — é mais direto.

## 1. Instalar o Docker

### Windows
1. Baixe o Docker Desktop em [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/)
2. Execute o instalador e siga as instruções na tela
3. Reinicie o computador se for solicitado
4. Abra o Docker Desktop e espere aparecer "Docker Desktop is running"

### Mac
1. Baixe o Docker Desktop em [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) (escolha a versão para o seu chip: Intel ou Apple Silicon)
2. Arraste o Docker para a pasta Aplicativos
3. Abra o Docker e espere aparecer "Docker Desktop is running"

### Linux (Ubuntu)
Abra o terminal e execute, uma linha de cada vez:
```bash
sudo apt update
sudo apt install docker.io docker-compose-v2
sudo systemctl start docker
sudo usermod -aG docker $USER
```
Depois do último comando, **feche e abra o terminal de novo**.

## 2. Confirmar que o Docker está funcionando

Abra o terminal (Prompt de Comando/PowerShell no Windows, Terminal no Mac/Linux) e digite:

```bash
docker --version
```

Deve aparecer um número de versão. Se aparecer erro, o Docker não foi instalado corretamente — repita o passo 1.

## 3. Instalar o Java 21 (para programar/rodar o backend)

Necessário para editar e testar o código sem precisar reconstruir o Docker a cada mudança.

### Windows
1. Baixe o JDK 21 em [adoptium.net](https://adoptium.net/) (Eclipse Temurin, versão 21, LTS)
2. Execute o instalador com as opções padrão
3. Confirme no terminal: `java -version`

### Mac
```bash
brew install openjdk@21
```
(se não tiver o Homebrew instalado, veja [brew.sh](https://brew.sh/))

### Linux (Ubuntu)
```bash
sudo apt install openjdk-21-jdk
```

Confirme em qualquer sistema:
```bash
java -version
```
Deve mostrar algo como `openjdk version "21..."`.

**Maven não precisa ser instalado separadamente** — o projeto já vem com o `mvnw` (Maven Wrapper), que baixa e usa a versão certa automaticamente.

## 4. Instalar o Git (se ainda não tiver)

- **Windows**: baixe em [git-scm.com](https://git-scm.com/) e instale com as opções padrão
- **Mac**: abra o terminal e digite `git --version` — se não estiver instalado, o próprio Mac vai oferecer para instalar
- **Linux**: `sudo apt install git`

## 5. Baixar (clonar) o projeto

No terminal, navegue até uma pasta onde você quer guardar o projeto (por exemplo, sua pasta de Documentos) e execute:

```bash
git clone https://github.com/SEU-USUARIO/limpeza-estofados.git
cd limpeza-estofados
```

(troque `SEU-USUARIO` pelo link real do repositório, que alguém do time pode te passar)

## 6. Criar o arquivo de configuração

O projeto precisa de um arquivo chamado `.env` com algumas configurações. Existe um modelo pronto:

```bash
cp .env.example .env
```

Abra o arquivo `.env` recém-criado em qualquer editor de texto e preencha os valores necessários (peça essas informações a alguém do time, já que envolvem senhas de teste).

## 7. Rodar o banco de dados via Docker

Para programar, você só precisa do banco em container — a aplicação roda direto pela sua IDE (mais rápido para ver mudanças):

```bash
docker compose up db
```

Deixe esse comando rodando num terminal.

## 8. Rodar a aplicação

Instale o **VS Code** ([code.visualstudio.com](https://code.visualstudio.com/)) e a extensão **"Extension Pack for Java"**. Abra a pasta `backend/` no VS Code — ele reconhece o projeto automaticamente e mostra um botão "Run" acima da classe `BackendApplication`.

Ou, pelo terminal, dentro de `backend/`:
```bash
./mvnw spring-boot:run
```

## 9. Testar se funcionou

Abra o navegador e acesse:

```
http://localhost:8080/swagger-ui.html
```

Se aparecer uma página com a lista de endpoints da API, deu tudo certo.

## 10. Parar tudo

Volte ao terminal onde a aplicação está rodando e pressione:

```
Ctrl + C
```

## Próximo passo: contribuindo

Depois de rodar o projeto com sucesso, veja [`CONTRIBUTING.md`](CONTRIBUTING.md) para o fluxo de branches, commits e como submeter suas mudanças.

## Problemas comuns

- **"port is already allocated"**: alguma outra aplicação já está usando a porta 8080 ou 5432 no seu computador. Feche outros programas que possam estar usando essas portas, ou peça ajuda para alguém do time.
- **Docker Desktop não abre / trava**: reinicie o computador e tente novamente.
- **Qualquer outro erro**: copie a mensagem completa que apareceu no terminal e peça ajuda no grupo do time — não tente adivinhar o que fazer.