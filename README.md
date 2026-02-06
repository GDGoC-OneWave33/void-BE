## 프로젝트 패키징 구조
```
project-root
├── core
│   ├── config       # 애플리케이션 전반 설정 (Spring Config, Bean 정의 등)
│   ├── filter       # 인증/인가, 로깅 등 공통 필터
│   └── infrastructure # 인프라 관련 핵심 로직 (DB, 외부 API 연동 등)
│
├── domain
│   └── ...          # 각 도메인별 패키지 구성 (DDD 기반)
│
└── shared
    ├── util         # 여러 도메인에서 공통으로 사용하는 유틸리티
    ├── dto          # 공통 DTO 정의
    └── exception    # 공통 예외 처리
```

