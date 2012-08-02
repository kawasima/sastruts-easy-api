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

未実装

## License

Easy APIはApache License 2.0 の元に配布されます。

* http://www.apache.org/licenses/LICENSE-2.0.txt