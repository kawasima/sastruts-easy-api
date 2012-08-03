SAStruts Easy API
=================

Easy API を使うと、APIのやり取りを簡単に実装することができます。
Annotationにしたがい、自動的にリクエスト内容をDtoに変換したり、DtoからXMLのレスポンスを生成したりできます。


## APIを提供する

例えばブログをポストするAPIを作るのは、以下のようにアノテーションに受け口のDto名を設定するだけです。

	public class BlogAction {
		public BlogDto blogDto;

		@Resource
		protected BlogService blogService;

		@Execute(validator=false)
		@EasyApi(requestDto="blogDto")
		public String postArticle() {
			blogService.insert(blogDto);
			return null;
		}
	}

## APIを使う

APIを実行するにはEasyApiClientを使います。

	@Resource
	protected EasyApiClient easyApiClient;

	@Execute(validator=false)
	public String executeApi() {
		List<UserDto> userList = easyApiClient.get(UserDto.class, query)
			.from("familyRegister")
			.getResultList();
	}

と、取得したいデータと取得先を指定して、getResultListを呼べば結果をDtoのリストで返してくれます。

取得先はdiconで定義します。

	<component name="familyRegister" class="net.unit8.sastruts.easyapi.client.EasyApiSetting">
		<property name="host">"http://example.com"</property>
		<property name="path">"/api/{id}"</property>
	</component>
	<component class="net.unit8.sastruts.easyapi.client.EasyApiSettingProvider">
		<property name="useMock">#ENV == 'ut' || #ENV == 'ct'</property>
		<initMethod name="register">
			<arg>{"familyRegister"}</arg>
		</initMethod>
	</component>

パスにはパラメータを含むことができます。これはEasyApiClientのgetメソッドの第2引数で渡すパラメータオブジェクト(JavaBeanまたはMap)にある
パラメータ名に合致すれば、API発行する際に自動で変換してくれます。

データを変更するAPIは次のように書きます。

	@Resource
	protected EasyApiClient easyApiClient;

	@Execute(validator=false)
	public String executeApi() {
		try {
			easyApiClient.post(MuchMoneyDto.class)
				.to("cityBank")
				.execute();
		} catch (TooRichException ex) {
			throw ActionMessagesException(ex.getMessageCode());
		}
	}


## License

Easy APIはApache License 2.0 の元に配布されます。

* http://www.apache.org/licenses/LICENSE-2.0.txt