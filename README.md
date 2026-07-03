# 📖 Entrelinhas App

O **Entrelinhas** é um aplicativo nativo para Android desenvolvido em **Jetpack Compose** para o gerenciamento pessoal de leituras. O projeto foi arquitetado seguindo o padrão **MVVM (Model-View-ViewModel)** e utiliza uma estratégia **Offline-First (Cache-First)** para garantir que o usuário tenha acesso aos seus livros mesmo sem conexão com a internet.

Por se tratar de uma versão focada na experiência direta do usuário admin, o aplicativo possui autenticação em segundo plano predefinida, exibindo diretamente a biblioteca do usuário conectado.

---

## 📱 Telas do Aplicativo

O fluxo do aplicativo é composto por **duas telas principais** integradas via *Navigation Compose*:

### 1. Home (`HomeScreen`)

A tela principal do aplicativo funciona como a estante do usuário, dividida em três categorias de leitura:

* 📖 **Lendo:** Livros que estão atualmente em progresso, exibindo uma barra visual de porcentagem lida (`LinearProgressIndicator`).
* ✅ **Lido:** Livros cuja leitura já foi concluída, exibindo as datas de início e término.
* 📌 **Desejado:** Lista de desejos com livros que o usuário pretende ler no futuro.

**Funcionalidades da Home:**

* Scroll horizontal (`LazyRow`) independente para cada categoria de livro.
* Clique em qualquer cartão de livro (`BookCard`) para abrir a tela de **Detalhamento do Livro** passando o identificador único.
* Botão Flutuante (FAB) para navegar diretamente para a tela de cadastro.

### 2. Detalhes do Livro (`BookDetailsScreen`)

Exibe de forma detalhada e imersiva todas as informações e estatísticas de uma obra específica obtida a partir do `BookRepository`.

* **Estados de UI Dinâmicos (`BookDetailUiState`):** Tratamento visual robusto para estados de *Loading* (indicador circular), *NotFound* (aviso de registro inexistente) e *Error* (com botão para tentar novamente).
* **Badge de Status:** Exibe o momento atual da leitura (*Lendo*, *Lido*, *Desejado*) com cores adaptativas obtidas dinamicamente.
* **Métricas Reativas:** Cartões informativos (`BookStatCard`) estruturados em grid para exibir o total de páginas, páginas lidas, ano de publicação e o período de leitura (datas de início e término formatadas).
* **Componente de Progresso:** Renderiza o `ReadingProgress` para dar um feedback visual instantâneo da evolução da leitura.
* **Seção de Sinopse:** Exibição amigável do texto descritivo do livro ou uma mensagem padrão caso nenhuma descrição tenha sido informada.

### 3. Cadastro de Livro (`CreateBookScreen`)

Formulário dinâmico para a inserção de novas obras literárias na estante.

* **Upload de Capa:** Integração nativa com a API do Android para abertura da **Galeria do Dispositivo** para seleção da foto de capa.
* **Formulário Completo:** Campos para título, autor, número total de páginas, páginas lidas e status.
* **Seleção de Datas:** Componente customizado `DateTimePicker` para definir com precisão o período da leitura.

---

## 🛠️ Arquitetura e Tecnologias

O projeto foi dividido de forma rigorosa em camadas para facilitar a manutenção e escalabilidade:

* **UI (Jetpack Compose & Material Design 3):** Componentização avançada utilizando `Scaffold`, `TopAppBar` e estados de UI reativos (`BooksUiStates` e `BookDetailUiState`).
* **Carregamento de Imagens Assíncrono:** Uso da biblioteca **Coil 3** (`AsyncImage`) para o carregamento fluido de capas remotas com suporte a corte proporcional (`ContentScale.Crop`).
* **Gerenciamento de Estado:** ViewModels utilizando `StateFlow` e inicialização *lazy* através do container central `EntrelinhasApplication`.
* **Persistência Local (Room Database):** Armazenamento em banco de dados SQLite local (`BookEntity`, `BookDao`) para prover o funcionamento offline do aplicativo.
* **Persistência Remota (Supabase API):**
* **GET / Rest API:** Sincronização e busca de livros individuais ou em lote salvos na nuvem.
* **POST / Rest API:** Envio de novas entidades de livros cadastrados.
* **Storage Service:** Bucket remoto para o upload e hospedagem das imagens de capa selecionadas na galeria.



---

## ⚙️ Como Configurar o Projeto

### Pré-requisitos

* Android Studio (versão Ladybug ou superior recomendada).
* Gradle configurado com suporte a KSP (Kotlin Symbol Processing).

### Configuração das Variáveis de Ambiente

Para que o aplicativo se comunique com o banco de dados e o Storage, crie um arquivo chamado `secrets.properties` na raiz do seu projeto (no mesmo nível do `local.properties`) e adicione as suas credenciais do Supabase:

```properties
supabase.url="https://SUA_URL_DO_SUPABASE.supabase.co"
supabase.key="SUA_ANON_OU_ADMIN_KEY"
admin.email="seu_usuario_admin@email.com"
admin.password="sua_senha_admin"

```

> ⚠️ **Nota de Segurança:** O arquivo `secrets.properties` está incluído no `.gitignore` e nunca deve ser enviado para repositórios públicos.

---

## 🧪 Como Testar o Fluxo Principal

1. **Testando a Home (Modo Online):** Ao abrir o app, o `HomeViewModel` disparará uma busca no Supabase (GET). Os livros serão cacheados no Room e exibidos organizadamente por status.
2. **Navegação e Detalhes:** Clique em um livro para inspecionar os detalhes. A tela de detalhes buscará o registro de forma isolada pelo ID.
3. **Fluxo de Cadastro:**
* Clique no botão de adicionar na Home.
* Toque na área da imagem para abrir a **Galeria**, escolha uma foto.
* Preencha os campos e salve. O app fará o upload da imagem para o Storage do Supabase, salvará o livro via POST, atualizará o banco local Room e retornará à Home atualizada.
4. **Testando o Comportamento Offline:** Coloque o celular em Modo Avião e feche o aplicativo. Ao abri-lo novamente, a estratégia **Cache-First** garantirá que todos os seus livros e dados de detalhamento continuem visíveis na tela inicial e na tela de detalhes.
