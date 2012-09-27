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

### APIサーバがEasyApiでない場合

EasyApiClientはEasyApi同士の通信以外でも利用できます。

	<component name="hotpepper" class="net.unit8.sastruts.easyapi.client.EasyApiSetting">
		<property name="host">"webservice.recruit.co.jp"</property>
		<property name="path">"/hotpepper/gourmet/v1/"</property>
		<property name="responseType">"plain"</property>
		<property name="rootElement">"results"</property>
	</component>

上記のように、responseType に plain を設定し、rootElement にXMLのルート要素名を設定してください。

## FAQ

### APIサーバがJSONやCSVを返してくるんだけど…

		<property name="responseFormat">"CSV"</property>

responseFormat というプロパティに、JSON または CSVを設定してください。
CSVの場合は1レコードが1つのDTOにマッピングされるので、getSingleResult での呼び出しは出来ません。
getResultList または iterate を使ってください。

### レスポンスサイズが非常に大きいんだけど…

getSingleResult、getResultList の代わりに、iterate を使ってください。

現在 responseFormat が CSV の場合のみサポートしていますが、1件ずつデシリアライズして処理することが可能です。
したがってメモリを大量消費することがありません。

		int res = client
				.get(PersonDto.class, query)
				.from("people")
				.iterate(new IterationCallback<PersonDto, Integer>() {
					int i=0;
					@Override
					public Integer iterate(PersonDto person, IterationContext context) {
						System.out.println((i++) + ":" + person.name);
						return i;
					}
				});

## License

Easy APIはApache License 2.0 の元に配布されます。

* http://www.apache.org/licenses/LICENSE-2.0.txt